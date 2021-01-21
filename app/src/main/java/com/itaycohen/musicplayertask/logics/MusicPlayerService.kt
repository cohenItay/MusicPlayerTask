package com.itaycohen.musicplayertask.logics

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerControlView
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.database.LocalDatabase
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import com.itaycohen.musicplayertask.ui.MainActivity
import com.itaycohen.musicplayertask.ui.Utils

class MusicPlayerService : LifecycleService() {

    private var serviceLooper: Looper? = null
    lateinit var mediaRepo: MediaItemsRepository
    lateinit var playerControlers: PlayerControlView
    lateinit var audioItemsList: List<AudioItem>

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(applicationContext)
    }

    private val modelAdapter: MediaItemsAdapter by lazy {
        MediaItemsAdapter()
    }

    private val serviceHandler: ServiceHandler by lazy {
        val handlerThread = HandlerThread("", Process.THREAD_PRIORITY_AUDIO)
        handlerThread.start()
        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = handlerThread.looper
        ServiceHandler(handlerThread.looper)
    }

    private val mediaPlayer: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(DefaultMediaSourceFactory(applicationContext))
            .build().also {
                it.addListener(playerListener)
            }
    }

    override fun onCreate() {
        super.onCreate()

        runAsForegoundService()
        val dao = LocalDatabase.getInstance(applicationContext).audioDao()
        mediaRepo = MediaItemsRepository.getInstance(applicationContext, dao)
        mediaRepo.fetchAudioItemsLiveData().observe(this, audioItemsObserver)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null) {
            val action = intent.getIntExtra(ACTION_KEY, -1)
            if (action == -1)
                return START_STICKY
            respondToControlsClick(action, startId)
        }
        serviceHandler.obtainMessage().also { msg ->
            msg.arg1 = startId
            serviceHandler.sendMessage(msg)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }


    private fun runAsForegoundService() {
        val notification: Notification = buildNotification(false)
        startForeground(Utils.MUSIC_SERVICE_NOTIFICATION_ID, notification)
    }

    private fun buildNotification(hasError: Boolean): Notification {
        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        val remoteView = RemoteViews(applicationContext.packageName, R.layout.player_notification_layout)
        getCurrentItem()?.also {
            remoteView.setTextViewText(R.id.audioTitleText, it.title)
            if (hasError)
                remoteView.setTextColor(R.id.audioTitleText, applicationContext.resources.getColor(R.color.red700, applicationContext.theme))
        }
        addAction(remoteView, ACTION_EXIT, R.id.exit)
        addAction(remoteView, ACTION_PLAY, R.id.exo_play)
        addAction(remoteView, ACTION_PAUSE, R.id.exo_pause)
        addAction(remoteView, ACTION_NEXT, R.id.exo_next)
        addAction(remoteView, ACTION_PREV, R.id.exo_prev)
        return NotificationCompat.Builder(applicationContext, Utils.CHANNEL_MAX_IMPORTANCE)
            .setContentTitle("נגן")
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .setContentIntent(pendingIntent)
            .setCustomBigContentView(remoteView)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun addAction(remoteView: RemoteViews, action: Int, viewId: Int) = with (remoteView) {
        val intent = Intent(applicationContext, MusicPlayerService::class.java)
        intent.putExtra(ACTION_KEY, action)
        val pending = PendingIntent.getService(applicationContext, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(viewId, pending)
    }

    private val audioItemsObserver = Observer<List<AudioItem>?> { audioItems ->
        audioItemsList = audioItems
        mediaPlayer.setMediaItems(modelAdapter.transform(audioItems))
        mediaPlayer.prepare()
        mediaPlayer.play()
    }

    private val playerListener = object: Player.EventListener {
        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            val stateStr = when (state) {
                Player.STATE_ENDED -> "ENDED"
                Player.STATE_BUFFERING -> "BUFF"
                Player.STATE_IDLE -> "IDLE"
                Player.STATE_READY -> "READY"
                else -> ""
            }
            Log.d("ttt", "onPlaybackStateChanged: $stateStr")
            if (state == Player.STATE_BUFFERING) {
                notificationManager.notify(Utils.MUSIC_SERVICE_NOTIFICATION_ID, buildNotification(
                    false
                )
                )
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            notificationManager.notify(Utils.MUSIC_SERVICE_NOTIFICATION_ID, buildNotification(true))
        }
    }

    private fun getCurrentItem() = mediaPlayer.currentMediaItem?.mediaId?.let { audioUrl ->
        audioItemsList.find { it.audioUrl == audioUrl }
    }

    private fun respondToControlsClick(action: Int, startId: Int) {
        when (action) {
            ACTION_EXIT -> stopSelf(startId)
            ACTION_PAUSE -> mediaPlayer.pause()
            ACTION_PLAY -> mediaPlayer.play()
            ACTION_NEXT -> mediaPlayer.next()
            ACTION_PREV -> mediaPlayer.previous()
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(15000)
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
        }
    }

    companion object {
        private const val ACTION_KEY = "action"
        private const val ACTION_EXIT = 1
        private const val ACTION_PLAY = 2
        private const val ACTION_PAUSE = 3
        private const val ACTION_NEXT = 4
        private const val ACTION_PREV = 5
    }

}
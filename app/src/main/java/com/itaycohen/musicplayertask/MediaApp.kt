package com.itaycohen.musicplayertask

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.itaycohen.musicplayertask.logics.AudioItemsInitLogic
import com.itaycohen.musicplayertask.ui.Utils

class MediaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AudioItemsInitLogic().run(applicationContext)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.desc_play)
            val descriptionText = getString(R.string.audio_player_secription)
            val importance = NotificationManager.IMPORTANCE_MAX
            val channel = NotificationChannel(Utils.CHANNEL_MAX_IMPORTANCE, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
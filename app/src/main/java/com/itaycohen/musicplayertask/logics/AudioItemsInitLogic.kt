package com.itaycohen.musicplayertask.logics

import android.content.Context
import android.util.Log
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.database.LocalDatabase
import com.itaycohen.musicplayertask.data.models.ItemImageInfo
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AudioItemsInitLogic {

    fun run(appContext: Context) {
        val dao = LocalDatabase.getInstance(appContext).audioDao()
        GlobalScope.launch(Dispatchers.Default) {
            Log.d("ttt", "run: ${dao.fetchAudioItemsIndices()}")
        }

        val prefs = appContext.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val isWelcomeAudiosAdded = prefs.getBoolean(WELCOME_AUDIOS_KEY, false)
        if (isWelcomeAudiosAdded)
            return


        val repo = MediaItemsRepository.getInstance(appContext, dao)
        CoroutineScope(Dispatchers.Default).launch {
            if (repo.fetchAudioItems().isEmpty()) { // Safety..
                val audioItems = createInitialAudioItems()
                dao.insertAll(*audioItems)
            }
            prefs.edit().putBoolean(WELCOME_AUDIOS_KEY, true).apply()
        }
    }

    private fun createInitialAudioItems() = arrayOf(
        AudioItem(
            "http://syntax.org.il/xtra/bob.m4a",
            ItemImageInfo.Local(R.drawable.ic_baseline_audiotrack_24),
            "bob"
        ),
        AudioItem(
            "http://syntax.org.il/xtra/bob1.m4a",
            ItemImageInfo.Local(R.drawable.ic_baseline_audiotrack_24),
            "bob1"
        ),
        AudioItem(
            "http://syntax.org.il/xtra/bob2.m4a",
            ItemImageInfo.Remote("https://cdn.iconscout.com/icon/premium/png-256-thumb/song-1680629-1428572.png"),
            "bob2"
        )
    )

    companion object {
        private const val SHARED_PREFS_FILE_NAME = "rumba_karamba"
        private const val WELCOME_AUDIOS_KEY = "isfirinstak"
    }
}
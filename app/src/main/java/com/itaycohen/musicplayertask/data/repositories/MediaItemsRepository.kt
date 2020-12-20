package com.itaycohen.musicplayertask.data.repositories

import android.content.Context
import com.itaycohen.musicplayertask.data.database.dao.AudioDao
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.models.AudioItemIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaItemsRepository private constructor(
    private val appContext: Context,
    private val audioDao: AudioDao
) {

    fun insertAudioItems(vararg audioItem: AudioItem) = audioDao.insertAll(*audioItem)

    suspend fun fetchAudioItems() = audioDao.fetchAudioItems()

    fun fetchAudioItemsLiveData() = audioDao.fetchAudioItemsLiveData()

    suspend fun insertOrReplaceIndicesFor(vararg audioItemsIndices: AudioItemIndex) =
        audioDao.insertOrReplaceIndicesFor(*audioItemsIndices)

    companion object {
        private lateinit var _instance: MediaItemsRepository
        fun getInstance(
            appContext: Context,
            audioDao: AudioDao
        ): MediaItemsRepository {
            if (!::_instance.isInitialized)
                _instance = MediaItemsRepository(appContext, audioDao)
            return _instance
        }
    }
}
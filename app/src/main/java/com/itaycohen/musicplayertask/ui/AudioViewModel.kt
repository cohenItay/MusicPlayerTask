package com.itaycohen.musicplayertask.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.itaycohen.musicplayertask.data.models.AdapterOperation
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.models.AudioItemIndex
import com.itaycohen.musicplayertask.data.models.ModelWithOperation
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import com.itaycohen.musicplayertask.logics.getDistinctUpdatesOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AudioViewModel(
    private val audioRepo: MediaItemsRepository
) : ViewModel() {

    private val changedRowsMap = mutableMapOf<String, Int>()

    val audioItemsLiveData: LiveData<ModelWithOperation<List<AudioItem>>> =
        MediatorLiveData<ModelWithOperation<List<AudioItem>>>().apply {
            addSource(audioRepo.fetchAudioItemsLiveData().getDistinctUpdatesOnly()) {
                this.value = ModelWithOperation(it, AdapterOperation.DataSetChange)
                Log.d("ttt", "update: ${it}")
            }
        }

    val itemTouchHelperCallback = object : TabsItemTouchHelperCallback.Callbacks{
        override fun changePosition(from: Int, to: Int): Boolean {
            val audioList = audioItemsLiveData.value?.model ?: return false
            if (from !in audioList.indices || to !in audioList.indices)
                return false

            changedRowsMap[audioList[from].audioUrl] = to
            changedRowsMap[audioList[to].audioUrl] = from
            audioItemsLiveData as MutableLiveData
            audioItemsLiveData.value = ModelWithOperation(audioList, AdapterOperation.Moved(from, to))
            return true
        }

        override fun onArrangementDoneForItem() {
            viewModelScope.launch(Dispatchers.Default) {
                val arr = changedRowsMap.map { entry ->
                    AudioItemIndex(entry.key, entry.value)
                }.toTypedArray()
                Log.d("ttt", "onArrangementDoneForItem: ${Arrays.toString(arr)}")
                audioItemsLiveData as MediatorLiveData
                val rows = audioRepo.insertOrReplaceIndicesFor(*arr)
                changedRowsMap.clear()
                Log.d("ttt", "onArrangementDoneForItem: ${rows}")
            }
        }
    }

    fun onAudioItemClick(v: View, bindingPosition: Int) {

    }

    class Factory(
        private val mediaItemsRepository: MediaItemsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val out =  if (modelClass == AudioViewModel::class.java)
                AudioViewModel(mediaItemsRepository)
            else
                null
            return out as T
        }
    }
}
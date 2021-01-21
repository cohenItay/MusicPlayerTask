package com.itaycohen.musicplayertask.ui

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.itaycohen.musicplayertask.logics.MusicPlayerService
import com.itaycohen.musicplayertask.data.models.*
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import com.itaycohen.musicplayertask.logics.getDistinctUpdatesOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class AudioViewModel(
    private val appContext: Context,
    private val audioRepo: MediaItemsRepository
) : ViewModel() {

    private var audioList = mutableListOf<AudioItem>()
    private var isInEditMode = false

    val audioItemsLiveData: LiveData<ModelWithOperation<List<AudioItem>>> =
        MediatorLiveData<ModelWithOperation<List<AudioItem>>>().apply {
            addSource(audioRepo.fetchAudioItemsLiveData().getDistinctUpdatesOnly()) {
                if (!isInEditMode) {
                    audioList = ArrayList(it)
                    this.value = ModelWithOperation(audioList, AdapterOperation.DataSetChange)
                }
            }
        }

    val itemTouchHelperCallback = object : TracksItemTouchHelperCallback.Callbacks{

        override fun onItemDragStart() {
            isInEditMode = true
        }

        override fun changePosition(from: Int, to: Int): Boolean {
            if (from !in audioList.indices || to !in audioList.indices || !isInEditMode)
                return false

            val temp = audioList[from]
            audioList[from] = audioList[to]
            audioList[to] = temp

            audioItemsLiveData as MutableLiveData
            audioItemsLiveData.value = ModelWithOperation(audioList, AdapterOperation.Moved(from, to))
            return true
        }

        override fun onItemDragEnd() {
            viewModelScope.launch(Dispatchers.Default) {
                val list = audioList.mapIndexed { index, audioItem ->
                    AudioItemIndex(audioItem.audioUrl, index)
                }
                audioItemsLiveData as MediatorLiveData
                audioRepo.insertOrReplaceIndicesFor(list)
                isInEditMode = false
            }
        }

        override fun onItemSwipToStart(adapterPosition: Int) =
            deleteAudioItem(adapterPosition)
    }

    fun onAudioItemClick(v: View, bindingPosition: Int) {
        v.findNavController().navigate(
            MediaListFragmentDirections.actionMediaListFragmentToDetailFragment(audioList[bindingPosition])
        )
    }

    fun onPlayClick(v: View)  = with (appContext) {
        val intent = Intent(this, MusicPlayerService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun deleteAudioItem(inPosition: Int) {
        isInEditMode = true
        viewModelScope.launch(Dispatchers.Default) {
            val totalDeleted = audioRepo.deleteAudioItem(audioList[inPosition])
            if (totalDeleted == 1) {
                audioList.removeAt(inPosition)
                audioItemsLiveData as MediatorLiveData
                audioItemsLiveData.postValue(ModelWithOperation(audioList, AdapterOperation.Removed(inPosition)))
            }
            isInEditMode = false
        }
    }

    class Factory(
        private val appContext: Context,
        private val mediaItemsRepository: MediaItemsRepository,
    ) : ViewModelProvider.Factory {



        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val out =  if (modelClass == AudioViewModel::class.java)
                AudioViewModel(appContext, mediaItemsRepository)
            else
                null
            return out as T
        }
    }
}
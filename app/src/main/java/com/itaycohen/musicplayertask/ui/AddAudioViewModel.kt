package com.itaycohen.musicplayertask.ui

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.webkit.URLUtil
import androidx.lifecycle.*
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.models.*
import com.itaycohen.musicplayertask.data.repositories.MediaItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class AddAudioViewModel(
    private val appContext: Context,
    private val audioRepo: MediaItemsRepository
) : ViewModel() {

    val formStateLiveData: LiveData<FormState> = MutableLiveData(FormState(mapOf(), QueryState.Idle))

    fun saveAudioIfValid(formMap: Map<Int, String>) {
        var audioItem = AudioItem("", ItemImageInfo.Remote(""), "")
        val stateMap = mutableMapOf<Int, FormEntryState>()
        formMap.forEach { (editTextId, content) ->
            val entryState = when (editTextId) {
                R.id.audioTitleEditText -> {
                    if (content.isEmpty())
                        FormEntryState.Invalid(appContext.getString(R.string.no_content))
                    else {
                        audioItem = audioItem.copy(title = content)
                        FormEntryState.Valid
                    }
                }
                R.id.audioUrlEditText  -> {
                    if (URLUtil.isValidUrl(content)) {
                        audioItem = audioItem.copy(audioUrl = content)
                        FormEntryState.Valid
                    } else {
                        FormEntryState.Invalid(appContext.getString(R.string.invalid_url))
                    }
                }
                R.id.audioImageUrlEditText -> {
                    if (URLUtil.isValidUrl(content)) {
                        audioItem = audioItem.copy(imageInfoData = ItemImageInfo.Remote(content))
                        FormEntryState.Valid
                    } else {
                        FormEntryState.Invalid(appContext.getString(R.string.invalid_url))
                    }
                }
                else -> throw IllegalArgumentException("Please add support for view id: $editTextId")
            }
            stateMap[editTextId] = entryState
        }
        formStateLiveData as MutableLiveData
        val isValid = !stateMap.values.any { it is FormEntryState.Invalid }
        if (isValid) {
            viewModelScope.launch(Dispatchers.Default) {
                var errorMessage: String? = null
                try {
                    audioRepo.insertAudioItems(audioItem)
                } catch (e: SQLiteException) {
                    errorMessage = when (e) {
                        is SQLiteConstraintException -> {
                            if (e.message?.contains("UNIQUE") == true)
                                appContext.getString(R.string.url_might_already_exists)
                            else
                                appContext.getString(R.string.cant_insert_audio)
                        }
                        else -> appContext.getString(R.string.cant_insert_audio)
                    }
                }
                formStateLiveData.postValue(
                    FormState(stateMap, errorMessage?.let {QueryState.Failure(it)} ?: QueryState.Success)
                )
            }
        } else {
            formStateLiveData.postValue(
                FormState(stateMap, QueryState.Failure(null))
            )
        }
    }

    class Factory(
        context: Context,
        private val mediaItemsRepository: MediaItemsRepository,
    ) : ViewModelProvider.Factory {

        private val appContext = context.applicationContext

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val out =  if (modelClass == AddAudioViewModel::class.java)
                AddAudioViewModel(appContext, mediaItemsRepository)
            else
                null
            return out as T
        }
    }
}
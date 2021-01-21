package com.itaycohen.musicplayertask.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.models.AudioItemIndex

@Dao
interface AudioDao {

    @Query("SELECT * FROM " +
            "audio_items_table AS a " +
            "LEFT JOIN audio_item_index AS i " +
            "ON i.owner_audio_url = a.audio_url " +
            "ORDER BY i.`index` ASC"
    )
    fun fetchAudioItemsLiveData() : LiveData<List<AudioItem>>

    @Query("SELECT * from audio_items_table")
    suspend fun fetchAudioItems() : List<AudioItem>

    @Query("SELECT * from audio_item_index")
    suspend fun fetchAudioItemsIndices() : List<AudioItemIndex>

    @Insert
    fun insertAll(vararg audioItems: AudioItem)

    @Delete
    fun delete(audioItem: AudioItem): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceIndicesFor(audioItemsIndices: List<AudioItemIndex>) : List<Long>
}
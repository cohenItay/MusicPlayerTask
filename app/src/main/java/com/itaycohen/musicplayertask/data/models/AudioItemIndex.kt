package com.itaycohen.musicplayertask.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_item_index",
    foreignKeys = [
        ForeignKey(
            entity = AudioItem::class,
            parentColumns = ["audio_url"],
            childColumns = ["owner_audio_url"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false
        )
    ]
)
data class AudioItemIndex (

    @PrimaryKey
    @ColumnInfo(name = "owner_audio_url")
    val ownerAudioUrl: String,

    @ColumnInfo(name = "index")
    val index: Int
)
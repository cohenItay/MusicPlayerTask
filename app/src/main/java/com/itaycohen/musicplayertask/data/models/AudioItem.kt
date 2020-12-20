package com.itaycohen.musicplayertask.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "audio_items_table")
@TypeConverters(ItemImageInfo.Converters::class)
data class AudioItem(

    @PrimaryKey
    @ColumnInfo(name = "audio_url")
    val audioUrl: String,

    val imageInfoData: ItemImageInfo,

    val title: String
)
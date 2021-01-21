package com.itaycohen.musicplayertask.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "audio_items_table")
@TypeConverters(ItemImageInfo.Converters::class)
data class AudioItem(

    @PrimaryKey
    @ColumnInfo(name = "audio_url")
    val audioUrl: String,

    val imageInfoData: ItemImageInfo,

    val title: String
) : Parcelable
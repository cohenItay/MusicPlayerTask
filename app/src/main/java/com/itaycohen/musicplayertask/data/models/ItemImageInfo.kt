package com.itaycohen.musicplayertask.data.models

import androidx.annotation.DrawableRes
import androidx.room.TypeConverter

sealed class ItemImageInfo {

    data class Local(
        @DrawableRes val drawableRes: Int
    ) : ItemImageInfo()

    data class Remote(
        val imageUrl: String
    ) : ItemImageInfo()

    // Room util:
    class Converters {
        @TypeConverter
        fun fromAudioImageInfo(value: ItemImageInfo): String {
            return when (value) {
                is ItemImageInfo.Local -> value.drawableRes.toString()
                is ItemImageInfo.Remote -> value.imageUrl
            }
        }

        @TypeConverter
        fun toAudioImageInfo(value: String): ItemImageInfo {
            return value.toIntOrNull()?.let { drawableRes ->
                ItemImageInfo.Local(drawableRes)
            } ?: ItemImageInfo.Remote(value)
        }
    }
}

package com.itaycohen.musicplayertask.data.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize

sealed class ItemImageInfo : Parcelable {

    @Parcelize
    data class Local(
        @DrawableRes val drawableRes: Int
    ) : ItemImageInfo(), Parcelable

    @Parcelize
    data class Remote(
        val imageUrl: String
    ) : ItemImageInfo(), Parcelable

    // Room util:
    class Converters {
        @TypeConverter
        fun fromAudioImageInfo(value: ItemImageInfo): String {
            return when (value) {
                is Local -> value.drawableRes.toString()
                is Remote -> value.imageUrl
            }
        }

        @TypeConverter
        fun toAudioImageInfo(value: String): ItemImageInfo {
            return value.toIntOrNull()?.let { drawableRes ->
                Local(drawableRes)
            } ?: Remote(value)
        }
    }
}

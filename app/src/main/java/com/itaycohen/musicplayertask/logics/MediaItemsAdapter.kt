package com.itaycohen.musicplayertask.logics

import com.google.android.exoplayer2.MediaItem
import com.itaycohen.musicplayertask.data.models.AudioItem

class MediaItemsAdapter {

    fun transform(audioItems: List<AudioItem>) : List<MediaItem> {
        return audioItems.map { audioItem ->
            MediaItem.Builder()
                .setUri(audioItem.audioUrl)
                .setMediaId(audioItem.audioUrl)
                .build()
        }
    }
}
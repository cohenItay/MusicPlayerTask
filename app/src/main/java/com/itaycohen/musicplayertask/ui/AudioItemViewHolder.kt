package com.itaycohen.musicplayertask.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.models.ItemImageInfo
import com.itaycohen.musicplayertask.databinding.MediaListItemBinding

class AudioItemViewHolder(
    v: View,
    private val interactionListener: Listener,
    private val itemTouchHelper: ItemTouchHelper
) : RecyclerView.ViewHolder(v), MovableViewHolder {

    private val binding = MediaListItemBinding.bind(v)

    init {
        v.setOnClickListener { interactionListener.onAudioItemClick(it , adapterPosition) }
        binding.root.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    itemTouchHelper.startSwipe(this)
                }
            }
            false
        }
        binding.dragBtn.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    itemTouchHelper.startDrag(this)
                }
                MotionEvent.ACTION_UP ->
                    v.performClick()
            }
            false
        }
    }

    override fun isMoveEnabled() = true

    fun bind(model: AudioItem) {
        with (binding) {
            mediaTitleTv.text = model.title
            when (val imageInfo = model.imageInfoData) {
                is ItemImageInfo.Local -> {
                    mediaImage.setImageResource(imageInfo.drawableRes)
                }
                is ItemImageInfo.Remote -> {
                    Glide.with(mediaImage)
                        .load(imageInfo.imageUrl)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .into(mediaImage)
                }
            }
        }
    }

    fun interface Listener {
        fun onAudioItemClick(v: View, bindingPosition: Int)
    }
}

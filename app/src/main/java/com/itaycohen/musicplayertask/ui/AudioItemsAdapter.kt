package com.itaycohen.musicplayertask.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.itaycohen.musicplayertask.R
import com.itaycohen.musicplayertask.data.models.AdapterOperation
import com.itaycohen.musicplayertask.data.models.AudioItem
import com.itaycohen.musicplayertask.data.models.ModelWithOperation

class AudioItemsAdapter(
    private val viewModel: AudioViewModel,
    lifecycleOwner: LifecycleOwner,
    private val itemTouchHelper: ItemTouchHelper
) : RecyclerView.Adapter<AudioItemViewHolder>() {

    private lateinit var inflater: LayoutInflater
    var audioItems: List<AudioItem> = emptyList()

    init {
        viewModel.audioItemsLiveData.observe(lifecycleOwner) { modelWithOperation ->
            audioItems = modelWithOperation.model
            when (val operation = modelWithOperation.operation) {
                is AdapterOperation.Added -> notifyItemInserted(operation.index)
                is AdapterOperation.Removed -> notifyItemRemoved(operation.index)
                is AdapterOperation.Moved -> notifyItemMoved(operation.from, operation.to)
                is AdapterOperation.DataSetChange -> notifyDataSetChanged()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioItemViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.media_list_item, parent, false)
        return AudioItemViewHolder(view, viewModel::onAudioItemClick, itemTouchHelper)
    }

    override fun onBindViewHolder(holder: AudioItemViewHolder, position: Int) {
        holder.bind(audioItems[position])
    }

    override fun getItemCount() = audioItems.size
}

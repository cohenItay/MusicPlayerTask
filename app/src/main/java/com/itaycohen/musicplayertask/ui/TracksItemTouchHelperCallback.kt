package com.itaycohen.musicplayertask.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 *  Helper class to drag items up or down
 */
class TracksItemTouchHelperCallback(
    private val callBacks: Callbacks
) : ItemTouchHelper.Callback() {


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START)

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder is MovableViewHolder) {

            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    callBacks.onItemDragStart()
                }
                ItemTouchHelper.ACTION_STATE_SWIPE -> {

                }
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = callBacks.changePosition(source.adapterPosition, target.adapterPosition)

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) : Boolean =
        (current as? MovableViewHolder)?.isMoveEnabled() == true && target is MovableViewHolder

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        callBacks.onItemDragEnd()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        when (i) {
            ItemTouchHelper.START ->
                callBacks.onItemSwipToStart(viewHolder.adapterPosition)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = true

    interface Callbacks {

        /**
         * Upon receiving this callback,
         * change the data position and notify the [RecyclerView.Adapter] using [RecyclerView.Adapter.notifyItemMoved]
         * @return true if the data has been changed successfully
         */
        fun changePosition(from: Int, to: Int) : Boolean

        fun onItemDragEnd()
        fun onItemDragStart()
        fun onItemSwipToStart(adapterPosition: Int)
    }
}
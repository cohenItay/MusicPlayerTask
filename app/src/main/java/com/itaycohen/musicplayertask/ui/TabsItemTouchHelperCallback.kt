package com.itaycohen.musicplayertask.ui

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.itaycohen.musicplayertask.R


/**
 *  Helper class to drag items up or down
 */
class TabsItemTouchHelperCallback(
    private val callBacks: Callbacks
) : ItemTouchHelper.Callback() {


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder is MovableViewHolder) {
            val ctx = viewHolder.itemView.context
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    // viewHolder.itemView.setBackgroundColor(R.color.gray200)
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
        callBacks.onArrangementDoneForItem()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {}
    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = false

    interface Callbacks {

        /**
         * Upon receiving this callback,
         * change the data position and notify the [RecyclerView.Adapter] using [RecyclerView.Adapter.notifyItemMoved]
         * @return true if the data has been changed successfully
         */
        fun changePosition(from: Int, to: Int) : Boolean

        fun onArrangementDoneForItem()
    }
}
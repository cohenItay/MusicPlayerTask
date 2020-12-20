package com.itaycohen.musicplayertask.ui

/**
 * Indicates that this ViewHolder position might change due to some user UI interaction
 */
interface MovableViewHolder {

    /**
     * @return true if the user can change drag this [MovableViewHolder].
     */
    fun isMoveEnabled(): Boolean
}
package com.itaycohen.musicplayertask.data.models

sealed class AdapterOperation {
    data class Removed(val index: Int): AdapterOperation()
    data class Added(val index: Int): AdapterOperation()
    data class Changed(val index: Int): AdapterOperation()
    data class Moved(val from: Int, val to: Int): AdapterOperation()
    object DataSetChange: AdapterOperation()
}
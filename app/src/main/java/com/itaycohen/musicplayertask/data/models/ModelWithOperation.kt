package com.itaycohen.musicplayertask.data.models

/**
 * A data class containing both the model and the required adapter operation.
 */
data class ModelWithOperation <out T> (
    val model: T,
    val operation: AdapterOperation? = null
)
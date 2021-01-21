package com.itaycohen.musicplayertask.data.models

@Suppress("DataClassPrivateConstructor")
sealed class QueryState {

    fun getStatusAsText() =
        when (this){
            is Failure -> "STATUS_FAILED"
            is Running -> "STATUS_RUNNING"
            is Success -> "STATUS_SUCCESS"
            is Idle -> "STATE_IDLE"
        }

    object Idle: QueryState()
    object Success: QueryState()
    object Running: QueryState()
    data class Failure(val errMsg: String?): QueryState()
}
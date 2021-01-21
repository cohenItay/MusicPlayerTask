package com.itaycohen.musicplayertask.data.models

data class FormState(
    val entriesStateMap: Map<Int, FormEntryState>,
    val saveState: QueryState
)
sealed class FormEntryState {
    data class Invalid(val reason: String): FormEntryState()
    object Idle: FormEntryState()
    object Valid: FormEntryState()
}

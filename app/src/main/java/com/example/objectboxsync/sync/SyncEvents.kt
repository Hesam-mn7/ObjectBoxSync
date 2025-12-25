package com.example.objectboxsync.sync

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Created by H.Mousavioun on 12/25/2025
 */
object SyncEvents {
    private val _refresh = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 32)
    val refresh = _refresh.asSharedFlow()

    fun notifyChanged() {
        _refresh.tryEmit(Unit)
    }
}
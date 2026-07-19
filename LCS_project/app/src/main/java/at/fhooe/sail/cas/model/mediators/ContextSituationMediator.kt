package at.fhooe.sail.cas.model.mediators

import at.fhooe.sail.cas.model.features.ContextSituation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ContextSituationMediator {

    private val _dataFlow = MutableSharedFlow<ContextSituation>(
        replay = 1,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dataFlow: SharedFlow<ContextSituation> = _dataFlow.asSharedFlow()

    suspend fun emitData(value: ContextSituation) {
        _dataFlow.emit(value)
    }
}
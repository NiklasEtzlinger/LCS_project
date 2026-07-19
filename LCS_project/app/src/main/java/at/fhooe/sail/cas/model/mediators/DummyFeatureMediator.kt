package at.fhooe.sail.cas.model.mediators

import at.fhooe.sail.cas.model.features.DummyFeature
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object DummyFeatureMediator {

    private val _dataFlow = MutableSharedFlow<DummyFeature>(
        replay = 1,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dataFlow: SharedFlow<DummyFeature> = _dataFlow.asSharedFlow()

    suspend fun emitData(value: DummyFeature) {
        _dataFlow.emit(value)
    }
}
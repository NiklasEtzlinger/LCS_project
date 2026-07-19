package at.fhooe.sail.cas.model.mediators

import at.fhooe.sail.cas.model.features.LocationFeature
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LocationFeatureMediator {

    private val _dataFlow = MutableSharedFlow<LocationFeature>(
        replay = 1,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dataFlow: SharedFlow<LocationFeature> = _dataFlow.asSharedFlow()

    suspend fun emitData(value: LocationFeature) {
        _dataFlow.emit(value)
    }
}
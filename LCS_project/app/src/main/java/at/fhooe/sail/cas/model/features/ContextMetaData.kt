package at.fhooe.sail.cas.model.features

import kotlinx.serialization.Serializable

@Serializable
data class ContextMetaData(
    val timestamp: Long = System.currentTimeMillis(),
    val duration: Int = -1
)
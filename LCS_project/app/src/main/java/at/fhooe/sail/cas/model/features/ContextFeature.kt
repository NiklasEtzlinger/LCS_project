package at.fhooe.sail.cas.model.features

import kotlinx.serialization.Serializable

@Serializable
data class ContextFeature(
    val id: String,
    val type: Int,
    val value: ContextValue,
    val meta: ContextMetaData? = null
)


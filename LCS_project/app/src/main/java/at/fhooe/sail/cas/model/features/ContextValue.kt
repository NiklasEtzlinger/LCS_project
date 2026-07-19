package at.fhooe.sail.cas.model.features

import kotlinx.serialization.Serializable

@Serializable
sealed class ContextValue {
    @Serializable
    data class StringValue(val v: String) : ContextValue()
    @Serializable
    data class IntValue(val v: Int) : ContextValue()
    @Serializable
    data class DoubleValue(val v: Double): ContextValue()
    @Serializable
    data class PairValue(val v0: ContextValue, val v1: ContextValue) : ContextValue()
}
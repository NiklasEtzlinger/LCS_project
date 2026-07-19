package at.fhooe.sail.cas.ui.viewmodel.rules

import kotlinx.serialization.Serializable

@Serializable
class RuleMethod(
    val name: String,
    val parameter: List<RuleParameter>
)

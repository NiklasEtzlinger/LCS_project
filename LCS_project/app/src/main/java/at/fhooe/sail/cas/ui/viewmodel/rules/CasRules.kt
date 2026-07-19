package at.fhooe.sail.cas.ui.viewmodel.rules

import kotlinx.serialization.Serializable

@Serializable
data class CasRules(
    val rules: List<Rule>
)

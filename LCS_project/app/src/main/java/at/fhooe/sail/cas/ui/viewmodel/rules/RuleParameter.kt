package at.fhooe.sail.cas.ui.viewmodel.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RuleParameter(
    @SerialName("p_name")
    val pName: String,
    @SerialName("p_type")
    val pType: String,
    @SerialName("p_value")
    val pValue: String,
)

package at.fhooe.sail.cas.ui.viewmodel.features

import android.graphics.Path

data class GeoFeature(
    val id: String = "undefined ID",
    val type: Int = -1,
    val attributes: Map<String, Any?> = mapOf(),
    val geometry: Path = Path()
)

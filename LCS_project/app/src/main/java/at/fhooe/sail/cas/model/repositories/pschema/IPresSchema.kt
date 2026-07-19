package at.fhooe.sail.cas.model.repositories.pschema

import android.graphics.Paint
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature

interface IPresSchema {
    fun getPresSchema(feature: GeoFeature, scale: Float): List<Paint>
    fun getLayers(): List<Triple<Int, String, String?>> // type, db table, where stmt
    fun getBackgroundColor(): Int = android.graphics.Color.LTGRAY // map background
}

data class LayerInfo(
    val layer: String,
    val filter: String?,
    val paints: List<PaintStyle> = listOf())

data class PaintStyle(
    val paint: Paint,
    val baseStrokeWidth: Float? = null
)
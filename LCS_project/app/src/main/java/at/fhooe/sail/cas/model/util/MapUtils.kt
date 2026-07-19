package at.fhooe.sail.cas.model.util

import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.toRect
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import kotlin.math.ceil
import kotlin.math.floor

fun Path.getPathBounds(): Rect {
    val rect: RectF = RectF()
    computeBounds(rect)
    return rect.toRect()
}

fun List<GeoFeature>.getBBox(): Rect {
    if (this.isEmpty()) return Rect()
    var rect: Rect = this[0].geometry.getPathBounds()
    for (i in 1 until size) {
        rect.union(this[i].geometry.getPathBounds())
    }
    return rect
}


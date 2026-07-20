package at.fhooe.sail.cas.model.util

import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.toRect
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import kotlin.math.ceil
import kotlin.math.cosh
import kotlin.math.floor
import kotlin.math.sqrt

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

/** Erdradius (EPSG:3857 / WGS84 Kugel) */
const val EARTH_RADIUS: Double = 6378137.0

/**
 * Reale Bodendistanz in Metern zwischen zwei Punkten in EPSG:3857.
 * Web-Mercator streckt Längen breitengradabhängig (bei Hagenberg um Faktor ~1.5),
 * daher wird die Kartendistanz um cosh(y/R) korrigiert.
 */
fun mercatorGroundDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val dx: Double = x2 - x1
    val dy: Double = y2 - y1
    val mercator: Double = sqrt(dx * dx + dy * dy)
    return mercator / cosh(((y1 + y2) / 2.0) / EARTH_RADIUS)
}


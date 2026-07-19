package at.fhooe.sail.cas.ui.viewmodel.features

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Rect
import androidx.core.graphics.createBitmap

data class PoiFeature(
    val id: String = "undefined poi id",
    val type: Int = 42,
    val x: Float = 0f,
    val y: Float = 0f,
    val icon: Bitmap = createBitmap(40, 40).apply {
        val c: Canvas = Canvas(this)
        c.drawColor(Color.RED)
        val p: Paint = Paint().apply {
            color = Color.BLACK
            style = Style.STROKE
        }
        c.drawRect(Rect(0, height, width, 0),p)
        c.drawText("?", width/2f, height/2f, p)
    },
    val name: String = "",
    val category: String = ""
)
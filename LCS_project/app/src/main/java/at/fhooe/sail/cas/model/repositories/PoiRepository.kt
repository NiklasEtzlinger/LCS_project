package at.fhooe.sail.cas.model.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.ui.viewmodel.features.PoiFeature

class PoiRepository(private val context: Context) {
    val pois: MutableList<PoiFeature> by lazy {
        mutableListOf(
            PoiFeature(
                "poi-01",
                1000, // parking
                1615849f,
                6168341f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-02",
                1000, // parking
                1615869f,
                6168309f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-03",
                1000, // parking
                1615841f,
                6168300f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-04",
                1000, // parking
                1615900f,
                6168215f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-05",
                1000, // parking
                1616000f,
                6168231f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-06",
                1000, // parking
                1615458f,
                6168318f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-07",
                1000, // parking
                1615360f,
                6168388f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
            PoiFeature(
                "poi-08",
                1000, // parking
                1615845f,
                6168394f,
                createDecoratedBitmap(60, R.drawable.parking_24dp)
            ), // poi
        )
    }

    fun fetchPois(): MutableList<PoiFeature> = pois

    private fun createDecoratedBitmap(size: Int, resId: Int): Bitmap {
        val drawable = context.getDrawable(resId)
            ?: throw IllegalArgumentException("Drawable not found")

        val bitmap = createBitmap(size, size)
        val canvas: Canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Parameter definieren
        val strokeWidth = 2f
        val cornerRadius = 10f
        // Der Offset muss genau die Hälfte der Rahmenbreite sein,
        // damit der Rahmen exakt innerhalb der Bitmap-Grenzen bleibt.
        val offset = strokeWidth / 2f

        // 2. Ein RectF für die Zeichenfläche definieren (leicht nach innen versetzt)
        val rect = RectF(offset,offset,
                         size.toFloat() - offset,size.toFloat() - offset
        )

        // 3. Hintergrund zeichnen (Fill)
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        // 4. Rand zeichnen (Stroke)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = Color.BLACK
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        // 5. Icon in die Mitte zeichnen
        val padding: Int = 8
        val iconSize = size - 2 * padding

        val left = (size - iconSize) / 2
        val top = (size - iconSize) / 2
        val right = left + iconSize
        val bottom = top + iconSize

        drawable.setBounds(left, top, right, bottom)
        drawable.draw(canvas)
        return bitmap
    }
}



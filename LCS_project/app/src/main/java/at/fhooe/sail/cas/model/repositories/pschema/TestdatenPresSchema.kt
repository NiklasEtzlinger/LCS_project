package at.fhooe.sail.cas.model.repositories.pschema

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import kotlin.collections.iterator

class TestdatenPresSchema : IPresSchema {

    private val presSchema = mutableMapOf<Int, LayerInfo>()

    init {
        presSchema[1000] = LayerInfo(
            layer = "punkte",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // fill Paint
                        color = Color.CYAN
                        style = Paint.Style.FILL
                    }
                ),
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.GRAY
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ), // paints
            filter = null
        ) // put 1000
        presSchema[2000] = LayerInfo(
            layer = "linien",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                    color = Color.YELLOW
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 32f
                ),
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.GREEN
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 20f
                ),
            ), // paints
            filter = null
        ) // put 2000
        presSchema[3000] = LayerInfo(
            layer = "flaechen",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // fill Paint
                        color = Color.BLUE
                        style = Paint.Style.FILL
                    }
                ),
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.RED
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ), // paint
            filter = null
        ) // put 3000
    }

    override fun getPresSchema(
        feature: GeoFeature,
        scale: Float
    ): List<Paint> {
        val layerInfo: LayerInfo =
            presSchema[feature.type] ?: run {// if null --> return default
                Log.e(TAG, "TestdatenPresSchema::getPresSchema() ... unsupported feature type(${feature.type}) encountered")
                return listOf(
                    // DEFAULT Paint style
                    Paint().apply { // stroke Paint
                        color = Color.BLACK
                        strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                    }
                )
            }
        val paints = mutableListOf<Paint>()
        layerInfo.paints.forEach { style ->
            if (style.baseStrokeWidth != null) {
                style.paint.strokeWidth = style.baseStrokeWidth / scale
            }
            paints.add(style.paint)
        }
        return paints
    }

    /*
    override fun getPoiSchema(type: Int): Bitmap =
        Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888).apply {
            val c: Canvas = Canvas(this)
            c.drawColor(Color.RED)
            val p: Paint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
            }
            c.drawRect(Rect(0,height, width, 0), p)
            c.drawText("?", width/2f, height/2f, p)
        }
    */

    override fun getLayers(): List<Triple<Int, String, String?>> {
        val res: MutableList<Triple<Int, String, String?>> = mutableListOf()
        for ((type, info) in presSchema) {
           res.add(Triple(type, info.layer, info.filter))
        }
        return res
    }
}


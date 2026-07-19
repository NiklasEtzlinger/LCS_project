package at.fhooe.sail.cas.model.repositories.pschema

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import kotlin.collections.iterator

class EmptyOSMPresSchema (private val context: Context) : IPresSchema {

    /**************************************************/
    /** Map stuff                                    **/
    /**************************************************/

    private val presSchema = mutableMapOf<Int, LayerInfo>()

    init {
        presSchema[6100] = LayerInfo(
            layer = "landuse-poly",
            filter = "landuse IN ('residential')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 6100 landuse-poly landuse IN ('residential')
        presSchema[6200] = LayerInfo(
            layer = "landuse-poly",
            filter = "landuse IN ('forest')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 6200 landuse-poly landuse IN ('forest')
        presSchema[6300] = LayerInfo(
            layer = "landuse-poly",
            filter = "landuse IN ('farmland', 'meadow')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 6300 landuse-poly landuse IN ('farmland', 'meadow')
        presSchema[6400] = LayerInfo(
            layer = "landuse-poly",
            filter = "landuse IN ('commercial', 'industrial')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 6400 landuse-poly landuse IN ('commercial', 'industrial')
        presSchema[6500] = LayerInfo(
            layer = "landuse-poly",
            filter = "landuse IN ('cemetery')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 6500 landuse-poly landuse IN ('cemetery')
        presSchema[5200] = LayerInfo(
            layer = "water-poly",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ),
            filter = null
        ) // 5200 water-poly null
        presSchema[5100] = LayerInfo(
            layer = "water-line",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ),
            filter = null
        ) // 5100 water-line null
        presSchema[4420] = LayerInfo(
            layer = "road",
            filter = "highway IN ('path', 'footpath')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4420 road highway IN ('path', 'footpath')
        presSchema[4410] = LayerInfo(
            layer = "road",
            filter = "highway IN ('track')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4410 road highway IN ('track')
        presSchema[4300] = LayerInfo(
            layer = "road",
            filter = "highway IN ('living_street', 'residential', 'unclassified', 'construction', 'service') AND ( tunnel IS NULL OR tunnel <> 'yes')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4300 road highway IN ('living_street', 'residential', 'unclassified', 'construction', 'service') AND ( tunnel IS NULL OR tunnel <> 'yes')
        presSchema[4200] = LayerInfo(
            layer = "road",
            filter = "highway IN ('primary', 'primary_link', 'secondary', 'secondary_link', 'tertiary') AND ( tunnel IS NULL OR tunnel <> 'yes')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4200 road highway IN ('primary', 'primary_link', 'secondary', 'secondary_link', 'tertiary') AND ( tunnel IS NULL OR tunnel <> 'yes')
        presSchema[4110] = LayerInfo(
            layer = "road",
            filter = "highway = 'motorway_link' AND ( tunnel IS NULL OR tunnel <> 'yes')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4110 road highway = 'motorway_link' AND ( tunnel IS NULL OR tunnel <> 'yes')
        presSchema[4100] = LayerInfo(
            layer = "road",
            filter = "highway = 'motorway' AND ( tunnel IS NULL OR tunnel <> 'yes')",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4100 road highway = 'motorway' AND ( tunnel IS NULL OR tunnel <> 'yes')
        presSchema[4000] = LayerInfo(
            layer = "road",
            filter = "tunnel IN ('yes')",

            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            )
        ) // 4000 road tunnel IN ('yes')
        presSchema[4900] = LayerInfo(
            layer = "railroad",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ),
            filter = null
        ) // 4900 railroad null
        presSchema[3000] = LayerInfo(
            layer = "building",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ),
            filter = null
        ) // 3000 building null
        presSchema[2000] = LayerInfo(
            layer = "landuse-point",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ),
            filter = null
        ) // 2000 landuse-point null
        presSchema[1000] = LayerInfo(
            layer = "verwaltung",
            paints = listOf(
                PaintStyle(
                    paint = Paint().apply { // stroke Paint
                        color = Color.BLACK
                        // strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                        isAntiAlias = true // performance bottleneck
                    },
                    baseStrokeWidth = 4f
                ),
            ),
            filter = null
        ) // 1000 verwaltung null
    }

    override fun getPresSchema(
        feature: GeoFeature,
        scale: Float
    ): List<Paint> {
        val layerInfo: LayerInfo =
            presSchema[feature.type] ?: // if null --> return default
                return listOf(
                    // DEFAULT Paint style
                    Paint().apply { // stroke Paint
                        color = Color.BLACK
                        strokeWidth = 4f / scale
                        style = Paint.Style.STROKE
                    }
                )
        val paints = mutableListOf<Paint>()
        layerInfo.paints.forEach { style ->
            if (style.baseStrokeWidth != null) {
                // style.paint.strokeWidth = (style.baseStrokeWidth / scale).coerceIn(1f, style.baseStrokeWidth * 20)
                style.paint.strokeWidth = style.baseStrokeWidth
            }
            paints.add(style.paint)
        }
        return paints
    }

    override fun getLayers(): List<Triple<Int, String, String?>> {
        val res: MutableList<Triple<Int, String, String?>> = mutableListOf()
        for ((type, info) in presSchema) {
           res.add(Triple(type, info.layer, info.filter))
        }
        return res
    }
}
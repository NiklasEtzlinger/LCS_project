package at.fhooe.sail.cas.model.repositories.pschema

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature

enum class ColorMode { DAY, NIGHT }

/**
 * OSM-style presentation schema with a day and a night palette.
 * Layer types and filters are identical to [EmptyOSMPresSchema];
 * only the paints differ. Stroke widths are given in world units
 * (metres in EPSG:3857) and converted to pixels with the current
 * map scale so roads grow/shrink naturally when zooming.
 */
class ColoredOSMPresSchema(
    private val context: Context,
    var mode: ColorMode = ColorMode.DAY
) : IPresSchema {

    private val daySchema = linkedMapOf<Int, LayerInfo>()
    private val nightSchema = linkedMapOf<Int, LayerInfo>()

    private fun fill(color: Long) = PaintStyle(
        paint = Paint().apply {
            this.color = color.toInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    )

    private fun stroke(color: Long, widthMeters: Float, dash: FloatArray? = null) = PaintStyle(
        paint = Paint().apply {
            this.color = color.toInt()
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
            dash?.let { pathEffect = DashPathEffect(it, 0f) }
        },
        baseStrokeWidth = widthMeters
    )

    private fun addLayer(
        type: Int, layer: String, filter: String?,
        day: List<PaintStyle>, night: List<PaintStyle>
    ) {
        daySchema[type] = LayerInfo(layer, filter, day)
        nightSchema[type] = LayerInfo(layer, filter, night)
    }

    init {
        // insertion order = draw order: landuse, water, roads, rail, buildings, points, boundary
        addLayer(
            6100, "landuse-poly", "landuse IN ('residential')",
            day = listOf(fill(0xFFE6E1DC)),
            night = listOf(fill(0xFF232830))
        )
        addLayer(
            6200, "landuse-poly", "landuse IN ('forest')",
            day = listOf(fill(0xFFB5D2A2)),
            night = listOf(fill(0xFF1E3324))
        )
        addLayer(
            6300, "landuse-poly", "landuse IN ('farmland', 'meadow')",
            day = listOf(fill(0xFFEAEFD8)),
            night = listOf(fill(0xFF262B20))
        )
        addLayer(
            6400, "landuse-poly", "landuse IN ('commercial', 'industrial')",
            day = listOf(fill(0xFFE9DCE8)),
            night = listOf(fill(0xFF2B2530))
        )
        addLayer(
            6500, "landuse-poly", "landuse IN ('cemetery')",
            day = listOf(fill(0xFFB9CDB6)),
            night = listOf(fill(0xFF253029))
        )
        addLayer(
            5200, "water-poly", null,
            day = listOf(fill(0xFFA6CBE0), stroke(0xFF8AB4CC, 1f)),
            night = listOf(fill(0xFF1E3A50), stroke(0xFF2A4C66, 1f))
        )
        addLayer(
            5100, "water-line", null,
            day = listOf(stroke(0xFF6FA8C9, 2.2f)),
            night = listOf(stroke(0xFF3E6480, 2.2f))
        )
        addLayer(
            4420, "road", "highway IN ('path', 'footpath')",
            day = listOf(stroke(0xFFD0654A, 1.6f, floatArrayOf(6f, 6f))),
            night = listOf(stroke(0xFF9E6B57, 1.6f, floatArrayOf(6f, 6f)))
        )
        addLayer(
            4410, "road", "highway IN ('track')",
            day = listOf(stroke(0xFF8A6D3B, 2f, floatArrayOf(12f, 7f))),
            night = listOf(stroke(0xFF7A6A4A, 2f, floatArrayOf(12f, 7f)))
        )
        addLayer(
            4300, "road",
            "highway IN ('living_street', 'residential', 'unclassified', 'construction', 'service') AND ( tunnel IS NULL OR tunnel <> 'yes')",
            day = listOf(stroke(0xFF8F8F8F, 7f), stroke(0xFFFFFFFF, 5f)),
            night = listOf(stroke(0xFF14181E, 7f), stroke(0xFF3C434E, 5f))
        )
        addLayer(
            4200, "road",
            "highway IN ('primary', 'primary_link', 'secondary', 'secondary_link', 'tertiary') AND ( tunnel IS NULL OR tunnel <> 'yes')",
            day = listOf(stroke(0xFFA87B2D, 9f), stroke(0xFFF7CE68, 6.5f)),
            night = listOf(stroke(0xFF6E5420, 9f), stroke(0xFFA8853B, 6.5f))
        )
        addLayer(
            4110, "road", "highway = 'motorway_link' AND ( tunnel IS NULL OR tunnel <> 'yes')",
            day = listOf(stroke(0xFF1B4A70, 7.5f), stroke(0xFF5C93C4, 5f)),
            night = listOf(stroke(0xFF16354E, 7.5f), stroke(0xFF3E6C99, 5f))
        )
        addLayer(
            4100, "road", "highway = 'motorway' AND ( tunnel IS NULL OR tunnel <> 'yes')",
            day = listOf(stroke(0xFF1B4A70, 11f), stroke(0xFF5C93C4, 8f)),
            night = listOf(stroke(0xFF16354E, 11f), stroke(0xFF3E6C99, 8f))
        )
        addLayer(
            4000, "road", "tunnel IN ('yes')",
            day = listOf(stroke(0xFF9E9E9E, 3f, floatArrayOf(14f, 8f))),
            night = listOf(stroke(0xFF565C68, 3f, floatArrayOf(14f, 8f)))
        )
        addLayer(
            4900, "railroad", null,
            day = listOf(stroke(0xFF707070, 2.6f), stroke(0xFFFFFFFF, 1.2f, floatArrayOf(10f, 10f))),
            night = listOf(stroke(0xFF909090, 2.6f), stroke(0xFF22262E, 1.2f, floatArrayOf(10f, 10f)))
        )
        addLayer(
            3000, "building", null,
            day = listOf(fill(0xFFD9C7B8), stroke(0xFFB39C88, 0.8f)),
            night = listOf(fill(0xFF343A46), stroke(0xFF454D5C, 0.8f))
        )
        addLayer(
            2000, "landuse-point", null,
            day = listOf(stroke(0xFF607080, 3f)),
            night = listOf(stroke(0xFF8090A0, 3f))
        )
        addLayer(
            1000, "verwaltung", null,
            day = listOf(stroke(0x809E5FA8, 2.5f, floatArrayOf(12f, 6f))),
            night = listOf(stroke(0x807E5F98, 2.5f, floatArrayOf(12f, 6f)))
        )
    }

    override fun getPresSchema(feature: GeoFeature, scale: Float): List<Paint> {
        val active = if (mode == ColorMode.NIGHT) nightSchema else daySchema
        val layerInfo: LayerInfo = active[feature.type]
            ?: return listOf(
                // DEFAULT Paint style for unknown layer types
                Paint().apply {
                    color = if (mode == ColorMode.NIGHT) Color.LTGRAY else Color.BLACK
                    strokeWidth = 2f
                    style = Paint.Style.STROKE
                }
            )
        val paints = mutableListOf<Paint>()
        layerInfo.paints.forEach { style ->
            if (style.baseStrokeWidth != null) {
                // world-unit width -> screen pixels, clamped to stay readable
                style.paint.strokeWidth = (style.baseStrokeWidth * scale).coerceIn(1f, 48f)
            }
            paints.add(style.paint)
        }
        return paints
    }

    override fun getLayers(): List<Triple<Int, String, String?>> {
        val res: MutableList<Triple<Int, String, String?>> = mutableListOf()
        for ((type, info) in daySchema) {
            res.add(Triple(type, info.layer, info.filter))
        }
        return res
    }

    override fun getBackgroundColor(): Int =
        if (mode == ColorMode.NIGHT) 0xFF14181E.toInt() else 0xFFF4F0E8.toInt()
}

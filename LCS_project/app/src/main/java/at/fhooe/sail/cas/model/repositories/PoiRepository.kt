package at.fhooe.sail.cas.model.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.createBitmap
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.viewmodel.features.PoiFeature
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.features.index.FeatureIndexManager
import mil.nga.geopackage.features.index.FeatureIndexResults
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.sf.GeometryEnvelope
import mil.nga.sf.util.GeometryEnvelopeBuilder

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

    fun fetchPois(): MutableList<PoiFeature> = (enrichedPois + gpkgPois).toMutableList()

    /**
     * The hardcoded parking POIs enriched with mock demo data (name,
     * category, description, rating) without touching the original list.
     */
    private val enrichedPois: List<PoiFeature> by lazy {
        pois.map { p ->
            p.copy(
                name = p.name.ifBlank { "Parking ${p.id.substringAfterLast('-')}" },
                category = p.category.ifBlank { "parking" },
                description = p.description.ifBlank { mockDescription("parking") },
                rating = if (p.rating > 0f) p.rating else mockRating(p.id)
            )
        }
    }

    /** mock demo description per amenity category */
    private fun mockDescription(amenity: String): String = when (amenity) {
        "parking" -> "Public parking area, free of charge."
        "restaurant" -> "Cozy local restaurant serving Austrian classics and seasonal dishes."
        "fast_food" -> "Quick bites: burgers, fries and snacks to go."
        "bar" -> "Relaxed bar with regional drinks and small snacks."
        "fuel" -> "Fuel station with convenience shop."
        "place_of_worship" -> "Historic church with regular services and quiet surroundings."
        "school" -> "Educational institution in the Hagenberg region."
        "fire_station" -> "Home of the local volunteer fire brigade."
        "recycling" -> "Recycling centre for household materials."
        "shelter" -> "Small covered shelter, a good spot for a break."
        else -> "Point of interest in Hagenberg."
    }

    /** stable mock rating in 3.0..5.0, derived from the POI id */
    private fun mockRating(id: String): Float =
        3f + (kotlin.math.abs(id.hashCode()) % 21) / 10f

    /**
     * POIs loaded from the GeoPackage: all buildings with an amenity tag
     * (restaurants, fuel stations, churches, schools, ...). Cached app-wide
     * so multiple repository instances parse the database only once.
     */
    private val gpkgPois: List<PoiFeature> by lazy {
        synchronized(PoiRepository::class.java) {
            cachedGpkgPois ?: loadPoisFromGeoPackage().also { cachedGpkgPois = it }
        }
    }

    private fun loadPoisFromGeoPackage(): List<PoiFeature> {
        val result: MutableList<PoiFeature> = mutableListOf()
        try {
            val manager = GeoPackageFactory.getManager(context)
            if (!manager.exists(DB_NAME)) {
                context.assets.open(DB_ASSET).use { inputStream ->
                    manager.importGeoPackage(DB_NAME, inputStream)
                }
            }
            val gp: GeoPackage = manager.open(DB_NAME)
            val featureDao: FeatureDao = gp.getFeatureDao(POI_LAYER)
            val featureIndex = FeatureIndexManager(context, gp, featureDao)
            val results: FeatureIndexResults = featureIndex.query(POI_FILTER)
            try {
                for (row in results) {
                    val geometryData = row.geometry ?: continue
                    if (geometryData.isEmpty) continue
                    val envelope: GeometryEnvelope =
                        GeometryEnvelopeBuilder.buildEnvelope(geometryData.geometry) ?: continue
                    val amenity: String = row.getValue("amenity")?.toString() ?: continue
                    val name: String = row.getValue("name")?.toString()
                        ?.takeIf { it.isNotBlank() } ?: amenity
                    val poiId: String = "gpkg-${row.getValue("fid")}"
                    result.add(
                        PoiFeature(
                            id = poiId,
                            type = typeFor(amenity),
                            x = ((envelope.minX + envelope.maxX) / 2.0).toFloat(),
                            y = ((envelope.minY + envelope.maxY) / 2.0).toFloat(),
                            icon = createDecoratedBitmap(60, iconFor(amenity)),
                            name = name,
                            category = amenity,
                            description = mockDescription(amenity),
                            rating = mockRating(poiId)
                        )
                    )
                }
            } finally {
                results.close()
                featureIndex.close()
                gp.close()
            }
            Log.i(TAG, "PoiRepository::loadPoisFromGeoPackage() --> ${result.size} POIs loaded")
        } catch (e: Exception) {
            Log.e(TAG, "PoiRepository::loadPoisFromGeoPackage() failed", e)
        }
        return result
    }

    private fun iconFor(amenity: String): Int = when (amenity) {
        "fuel" -> R.drawable.gas_station_24dp
        "parking", "parking_entrance" -> R.drawable.parking_24dp
        else -> R.drawable.poi_24dp
    }

    private fun typeFor(amenity: String): Int = when (amenity) {
        "fuel" -> 1100
        "restaurant", "fast_food", "bar", "cafe" -> 1200
        "place_of_worship" -> 1300
        "school" -> 1400
        "fire_station" -> 1500
        else -> 1900
    }

    companion object {
        private const val DB_NAME = "Hagenberg-3857"
        private const val DB_ASSET = "Gemeinde-Hagenberg-3857.gpkg"
        private const val POI_LAYER = "building"
        private const val POI_FILTER = "amenity IS NOT NULL AND amenity <> ''"

        @Volatile
        private var cachedGpkgPois: List<PoiFeature>? = null
    }

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



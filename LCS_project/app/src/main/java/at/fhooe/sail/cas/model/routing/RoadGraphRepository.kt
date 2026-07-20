package at.fhooe.sail.cas.model.routing

import android.content.Context
import android.util.Log
import at.fhooe.sail.cas.TAG
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.features.index.FeatureIndexManager
import mil.nga.geopackage.features.index.FeatureIndexResults
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.sf.Geometry
import mil.nga.sf.LineString
import mil.nga.sf.MultiLineString

/**
 * Lädt die Straßenlinien aus dem GeoPackage und baut daraus einmalig den
 * [RoadGraph]. Der Graph wird app-weit zwischengespeichert – der Aufbau kostet
 * einige hundert Millisekunden und muss daher aus einem Hintergrund-Thread
 * angestoßen werden.
 */
object RoadGraphRepository {

    private const val DB_NAME = "Hagenberg-3857"
    private const val DB_ASSET = "Gemeinde-Hagenberg-3857.gpkg"
    private const val ROAD_LAYER = "road"

    /** Fußgänger-Netz: Autobahnen und Baustellen ausschließen */
    private const val ROAD_FILTER =
        "(highway IS NULL OR highway NOT IN ('motorway', 'motorway_link', 'construction'))"

    @Volatile
    private var cached: RoadGraph? = null

    /** Blockierend – nur aus einem Hintergrund-Dispatcher aufrufen. */
    fun getGraph(context: Context): RoadGraph? {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: buildGraph(context)?.also { cached = it }
        }
    }

    private fun buildGraph(context: Context): RoadGraph? {
        val startedAt: Long = System.currentTimeMillis()
        try {
            val manager = GeoPackageFactory.getManager(context)
            if (!manager.exists(DB_NAME)) {
                context.assets.open(DB_ASSET).use { inputStream ->
                    manager.importGeoPackage(DB_NAME, inputStream)
                }
            }
            val gp: GeoPackage = manager.open(DB_NAME)
            val featureDao: FeatureDao = gp.getFeatureDao(ROAD_LAYER)
            val featureIndex = FeatureIndexManager(context, gp, featureDao)
            val results: FeatureIndexResults = featureIndex.query(ROAD_FILTER)

            val builder = RoadGraph.Builder()
            try {
                for (row in results) {
                    val geometryData = row.geometry ?: continue
                    if (geometryData.isEmpty) continue
                    collectLines(geometryData.geometry, builder)
                }
            } finally {
                results.close()
                featureIndex.close()
                gp.close()
            }

            val graph: RoadGraph = builder.build()
            Log.i(
                TAG,
                "RoadGraphRepository::buildGraph() --> ${graph.nodeCount} Knoten, " +
                        "${graph.edgeCount} Kanten, " +
                        "${graph.mainComponentNodeCount} im Hauptnetz " +
                        "(${System.currentTimeMillis() - startedAt} ms)"
            )
            return graph
        } catch (e: Exception) {
            Log.e(TAG, "RoadGraphRepository::buildGraph() failed", e)
            return null
        }
    }

    private fun collectLines(geometry: Geometry, builder: RoadGraph.Builder) {
        when (geometry) {
            is LineString -> builder.addLine(
                geometry.points.map { doubleArrayOf(it.x, it.y) }
            )
            is MultiLineString -> geometry.lineStrings.forEach { collectLines(it, builder) }
            else -> { /* für das Routing irrelevante Geometrietypen */ }
        }
    }
}

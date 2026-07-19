package at.fhooe.sail.cas.model.repositories

import android.content.Context
import android.graphics.Path
import android.util.Log
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.model.repositories.pschema.IPresSchema
import at.fhooe.sail.cas.model.repositories.pschema.TestdatenPresSchema
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import mil.nga.geopackage.BoundingBox
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageFactory
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.features.index.FeatureIndexManager
import mil.nga.geopackage.features.index.FeatureIndexResults
import mil.nga.geopackage.features.user.FeatureDao
import mil.nga.sf.Geometry
import mil.nga.sf.GeometryType
import mil.nga.sf.LineString
import mil.nga.sf.MultiLineString
import mil.nga.sf.MultiPolygon
import mil.nga.sf.Point
import mil.nga.sf.Polygon

class TestdatenGeoPackageRepository(private val context: Context) {
    //
    // add LIB Dependency
    // mil.nga.geopackage:geopackage-android

    private val manager: GeoPackageManager =  GeoPackageFactory.getManager(context)
    private var geoPackage: GeoPackage? = null

    private var initialBBox: BoundingBox? = null

    fun initRepository(dbName: String, fileName: String, initBBox: BoundingBox?) {
        if (!manager.exists(dbName)) {
            context.assets.open(fileName).use { inputStream ->
                manager.importGeoPackage(dbName, inputStream)
            }
        }
        initialBBox = initBBox
    }

    fun openRepository(dbName: String) {
        geoPackage = manager.open(dbName)
    }

    fun closeRepository() {
        geoPackage?.close()
    }

    fun fetchFeatures(
        layerInfos: List<Triple<Int, String, String?>>? = null,
        bbox: BoundingBox? = null
    ): List<GeoFeature> {
        val resultList: MutableList<GeoFeature>  = mutableListOf<GeoFeature>()

        layerInfos?.forEach { layerInfo ->
            Log.i(TAG, "GeoPackageRepository::fetchFeatures() ... getting layer (${layerInfo.second})")
            val gp: GeoPackage = geoPackage ?: return mutableListOf()
            // access DB table
            val featureDao: FeatureDao = gp.getFeatureDao(layerInfo.second)

            // Räumliche Abfrage über den R-Tree Index des GeoPackages
            val featureIndex: FeatureIndexManager = FeatureIndexManager(
                context,
                gp,
                featureDao
            )
            // IF bbox, use it + WHERE to restrict result set ELSE just use WHERE ELSE get all
            val results: FeatureIndexResults =
                bbox?.let { featureIndex.query(it, layerInfo.third) }
                    ?: featureIndex.query(layerInfo.third)

            try {
                val iterator = results.iterator()
                while (iterator.hasNext()) {
                    val featureRow = iterator.next()
                    val geometryData = featureRow.geometry
                    var type: Int = -1
                    if (geometryData != null && !geometryData.isEmpty) {

                        val geometry = geometryData.geometry
                        val path = Path().apply {
                            fillType = Path.FillType.EVEN_ODD
                        }

                        when (geometry.geometryType) {
                            GeometryType.POINT -> {
                                val pt = geometry as Point
                                type = 1000
                                path.addCircle(
                                    pt.x.toFloat(),
                                    pt.y.toFloat(),
                                    1f,
                                    Path.Direction.CW)
                            }

                            GeometryType.LINESTRING, GeometryType.MULTILINESTRING -> {
                                type = 2000
                                addLineToPath(path, geometry)
                            }

                            GeometryType.POLYGON, GeometryType.MULTIPOLYGON -> {
                                type = 3000
                                addPolygonToPath(path, geometry)
                            }
                            else -> {
                                Log.w(
                                    TAG,
                                    "TestdatenGeoPackage::fetchFeatures() --> unexpected geometry type encountered")
                            }
                        } // when

                        // Attribute extrahieren
                        val attrs = mutableMapOf<String, Any?>()
                        featureRow.columnNames.forEach { col ->
                            attrs[col] = featureRow.getValue(col)
                        }
                        val feature: GeoFeature =
                            GeoFeature(
                                id = featureRow.getValue("iso") as String,
                                type = type,
                                attributes = mapOf(),
                                geometry = path)
                        Log.i(TAG, "GeoPackageRepository::fetchFeatures() add new $feature ")
                        resultList.add(feature)
                    }
                }
            } catch (e: Exception) {
                Log.e("GeoRepo", "Fehler beim Lesen des Cursors", e)
            } finally {
                results.close()
                featureIndex.close()
            }
        }
        return resultList
    }

    private fun addLineToPath(path: Path, geometry: Geometry) {
        if (geometry is LineString) {
            val points = geometry.points
            if (points.isNotEmpty()) {
                path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
                for (i in 1 until points.size) {
                    path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
                }
            }
        } else if (geometry is MultiLineString) {
            geometry.lineStrings.forEach { addLineToPath(path, it) }
        }
    }

    private fun addPolygonToPath(path: Path, geometry: Geometry) {
        if (geometry is Polygon) {
            // Exterior Ring (Außenhülle)
            geometry.rings.forEach { ring ->
                val points = ring.points
                if (points.isNotEmpty()) {
                    path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
                    }
                    path.close() // Schließt den Ring -> Erzeugt Loch, wenn es ein Inner Ring ist
                }
            }
        } else if (geometry is MultiPolygon) {
            geometry.polygons.forEach { addPolygonToPath(path, it) }
        }
    }

    fun getPresSchema(): IPresSchema = TestdatenPresSchema()
    fun getInitBBox(): BoundingBox?  = initialBBox
}



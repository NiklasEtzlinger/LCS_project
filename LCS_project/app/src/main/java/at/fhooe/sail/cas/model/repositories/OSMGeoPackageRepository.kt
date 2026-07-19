package at.fhooe.sail.cas.model.db

import android.content.Context
import android.util.Log
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.model.repositories.IGeoFeatureRepository
import at.fhooe.sail.cas.model.repositories.pschema.EmptyOSMPresSchema
import at.fhooe.sail.cas.model.repositories.pschema.IPresSchema
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import mil.nga.geopackage.BoundingBox
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageFactory
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

class OSMGeoPackageRepository(private val context: Context) : IGeoFeatureRepository {
    //
    // add LIB Dependency
    // mil.nga.geopackage:geopackage-android

    private val manager = GeoPackageFactory.getManager(context)

    private var geoPackage: GeoPackage? = null

    private var bbox: BoundingBox? = null

    // db.first = DB name; db.second = DB data file name
    override fun initRepository(dbName: String, fileName: String, bbox: BoundingBox?) {
        // Prüfen, ob die Karte bereits importiert wurde
        this.bbox = bbox
        if (!manager.exists(dbName)) {
            context.assets.open(fileName).use { inputStream ->
                // importGeoPackage kopiert den Stream in das interne Datenbank-Verzeichnis der App
                manager.importGeoPackage(dbName, inputStream)
            }
        }
    }

    override fun openRepository(dbName: String) {
        Log.i(TAG, "GeoPackageRepository::openGeoDB open GeoPackage repository ($dbName)")
        geoPackage = manager.open(dbName)
    }

    override fun closeRepository() {
        Log.i(TAG, "GeoPackageRepository::close()")
        geoPackage?.close()
    }

    override fun fetchFeatures(
        layerInfos: List<Triple<Int, String, String?>>?,
        boundingBox: BoundingBox?
    ): MutableList<GeoFeature>  {
        // Log.i(TAG, "OSMGeoPackageRepository::fetchFeatures() ... ")
        val resultList: MutableList<GeoFeature>  = mutableListOf<GeoFeature>()

        layerInfos?.forEach { layerInfo ->
            // Log.i(TAG, "OSMGeoPackageRepository::fetchFeatures() ... getting layer (${layerInfo.second})")
            val gp: GeoPackage = geoPackage ?: return mutableListOf()
            val featureDao: FeatureDao = gp.getFeatureDao(layerInfo.second)

            // Räumliche Abfrage über den R-Tree Index des GeoPackages
            val featureIndex: FeatureIndexManager = FeatureIndexManager(
                context,
                gp,
                featureDao
            )
            val results: FeatureIndexResults =
                boundingBox?.let { featureIndex.query(it,layerInfo.third, null) }
                    ?: featureIndex.query(layerInfo.third)

            try {
                val iterator = results.iterator()
                // Log.d(TAG, "found ${results.count()} entries for ${layerInfo.second}")
                while (iterator.hasNext()) {
                    val featureRow = iterator.next()
                    val geometryData = featureRow.geometry

                    if (geometryData != null && !geometryData.isEmpty) {

                        val geometry = geometryData.geometry
                        val path = android.graphics.Path().apply {
                            fillType = android.graphics.Path.FillType.EVEN_ODD
                        }

                        when (geometry.geometryType) {
                            GeometryType.POINT -> {
                                val pt = geometry as Point
                                path.addCircle(
                                    pt.x.toFloat(),
                                    pt.y.toFloat(),
                                    1f,
                                    android.graphics.Path.Direction.CW)
                            }
                            GeometryType.LINESTRING, GeometryType.MULTILINESTRING -> {
                                addLineToPath(path, geometry)
                            }
                            GeometryType.POLYGON, GeometryType.MULTIPOLYGON -> {
                                addPolygonToPath(path, geometry)
                            }
                            else -> {
                                Log.w(
                                    TAG,
                                    "OSMGeoPackageRepository::fetchFeatures() --> unexpected geometry type encountered")
                            }
                        }

                        // Attribute extrahieren
                        val attrs = mutableMapOf<String, Any?>()
                        featureRow.columnNames.forEach { col ->
                            attrs[col] = featureRow.getValue(col)
                        }
                        val feature: GeoFeature =
                            GeoFeature(
                                id = featureRow.getValue("fid").toString(),
                                type = layerInfo.first,
                                attributes = mapOf(),
                                geometry = path)
                        // Log.i(TAG, "OSMGeoPackageRepository::fetchFeatures() add new $feature ")
                        resultList.add(feature)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "OSMGeoPackageRepository::fetchFeatures() ... Fehler beim Lesen des Cursors", e)
            } finally {
                results.close()
                featureIndex.close()
                // Log.i(TAG, "OSMGeoPackageRepository::fetchFeatures() ... close called")
            }
        } // layerInfo
        return resultList
    }

    override fun getPresSchema(): IPresSchema = EmptyOSMPresSchema(context)

    override fun getInitBBox(): BoundingBox? = bbox

    private fun addLineToPath(path: android.graphics.Path, geometry: Geometry) {
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

    private fun addPolygonToPath(path: android.graphics.Path, geometry: Geometry) {
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
}
package at.fhooe.sail.cas.model.repositories

import at.fhooe.sail.cas.model.repositories.pschema.IPresSchema
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import mil.nga.geopackage.BoundingBox

interface IGeoFeatureRepository {
    fun initRepository(dbName: String, fileName: String, initBoundingBox: BoundingBox?)
    fun openRepository(dbName: String)
    fun closeRepository()
    fun fetchFeatures(
        layerInfos: List<Triple<Int, String, String?>>? = null,
        bbox: BoundingBox? = null
    ): List<GeoFeature>
    fun getPresSchema(): IPresSchema
    fun getInitBBox(): BoundingBox?
}
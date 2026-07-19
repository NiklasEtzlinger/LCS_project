package at.fhooe.sail.cas.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.fhooe.sail.android.dsl_parser.tree.context.ContextObject
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.model.db.OSMGeoPackageRepository
import at.fhooe.sail.cas.model.features.ContextElementWrapper
import at.fhooe.sail.cas.model.features.ContextSituation
import at.fhooe.sail.cas.model.mediators.ContextSituationMediator
import at.fhooe.sail.cas.model.mediators.LocationFeatureMediator
import at.fhooe.sail.cas.model.mediators.MainServiceMediator
import at.fhooe.sail.cas.model.repositories.IGeoFeatureRepository
import at.fhooe.sail.cas.model.repositories.PoiRepository
import at.fhooe.sail.cas.model.repositories.pschema.ColorMode
import at.fhooe.sail.cas.model.repositories.pschema.ColoredOSMPresSchema
import at.fhooe.sail.cas.model.repositories.pschema.IPresSchema
import at.fhooe.sail.cas.model.service.IMainService
import at.fhooe.sail.cas.model.util.getBBox
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature
import at.fhooe.sail.cas.ui.viewmodel.features.LocationFeature
import at.fhooe.sail.cas.ui.viewmodel.features.PoiFeature
import at.fhooe.sail.cas.ui.viewmodel.rules.CasRules
import at.fhooe.sail.cas.ui.viewmodel.rules.DynamicExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mil.nga.geopackage.BoundingBox
import mil.nga.proj.Projection
import mil.nga.proj.ProjectionFactory
import mil.nga.proj.ProjectionTransform
import org.locationtech.proj4j.ProjCoordinate
import kotlin.coroutines.coroutineContext
import kotlin.math.min

const val DELTA_DRAG: Float = 50f
const val DELTA_ZOOM: Float = 1.5f
const val MAX_TRAIL_POINTS: Int = 2000
const val TAP_RADIUS_PX: Float = 60f

@OptIn(InternalCoroutinesApi::class)
class MapViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Service Stuff
     */

    val service: IMainService? by lazy { MainServiceMediator.getInstance() }

    /**
     * Location Stuff
     */
    val drawLocation: Boolean = true

    var locList: MutableList<LocationFeature> = mutableListOf()

    /** follow-me: keep the map centred on incoming GPS fixes */
    var followLocation: Boolean by mutableStateOf(false)

    /** breadcrumb trail of received fixes (EPSG:3857) */
    val trail: MutableList<LocationFeature> = mutableListOf()

    init {
        viewModelScope.launch {
            LocationFeatureMediator.dataFlow.collect {
                loc: at.fhooe.sail.cas.model.features.LocationFeature ->
                val vmLocation: LocationFeature = convertToUiModel(loc)
                synchronized(locList) {
                    locList.clear()
                    locList.add(vmLocation)
                    trail.add(vmLocation)
                    if (trail.size > MAX_TRAIL_POINTS) {
                        trail.removeAt(0)
                    }
                }
                if (followLocation) {
                    centerOnLocation()
                } else if (drawLocation) {
                    repaintAsync()
                }
            }
        }
    }

    fun toggleFollowLocation() {
        followLocation = !followLocation
        if (followLocation) {
            centerOnLocation()
        }
    }

    fun centerOnLocation() {
        val loc: LocationFeature = synchronized(locList) { locList.lastOrNull() } ?: return
        val screenPt = FloatArray(2)
        matrix.mapPoints(screenPt, floatArrayOf(loc.x, loc.y))
        val (cX, cY) = getCanvasCentre()
        matrix.postTranslate(cX - screenPt[0], cY - screenPt[1])
        repaintAsync()
    }

    fun convertToUiModel(dmLoc: at.fhooe.sail.cas.model.features.LocationFeature) : LocationFeature {
        val wgs84:       Projection = ProjectionFactory.getProjection(4326)
        val webMercator: Projection = ProjectionFactory.getProjection(3857)

        val transfrom: ProjectionTransform = wgs84.getTransformation(webMercator)
        val mPt: ProjCoordinate = ProjCoordinate(dmLoc.longitude, dmLoc.latitude)
        val vmPt: ProjCoordinate = transfrom.transform(mPt)
        return LocationFeature(vmPt.x.toFloat(), vmPt.y.toFloat())
    }

    /**
     * ContextSituation & Rule Stuff
     */
    lateinit var ruleSet: CasRules

    fun inflateRules(context: Context) {
        val content: String = context.assets
            .open("cas_map_rules.json")
            .bufferedReader()
            .use { it.readText() }

        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        ruleSet = json.decodeFromString(content)

        ruleSet.rules.forEach { rule ->
            Log.i(TAG, "MapViewModel::inflateRules() --> inflating rule: $rule")
            rule.inflate()
        }
    }


    var situation: ContextSituation? = null

    init {
        inflateRules(application)
        viewModelScope.launch {
            ContextSituationMediator.dataFlow.collect { sit ->
                Log.i(TAG, "MapViewModel::ContextSituationFlow::collect() --> $sit")
                val contextObjects: MutableList<ContextObject> = mutableListOf()
                sit.contextElements.forEach { cF ->
                    val cO: ContextObject? = ContextElementWrapper.toJavaContextObject(cF)
                    if (cO != null) {
                        contextObjects.add(cO)
                    }
                }

                ruleSet.rules.forEach { rule ->
                    rule.conditionTree?.clear()
                    rule.conditionTree?.setVariableParameters(contextObjects.toTypedArray())
                    try {
                        val res = rule.conditionTree?.calculate()
                        if (res is Boolean) {
                            Log.i(TAG, "MapViewModel::ContextSituation::eval (${rule.condition}) --> $res")
                            if (res) {
                                val valueArray: Array<String> = rule.action.ruleMethod.parameter.map { it.pValue }.toTypedArray()
                                DynamicExecutor.execute(
                                    instance = this@MapViewModel,
                                    methodName = rule.action.ruleMethod.name,
                                    paramValues = valueArray
                                )
                            }
                        } else {
                            Log.e(TAG, "ERROR in calculate")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "ERROR in calculate (outer)")
                    }
                }
            }
        }
    }

    fun setColorMode(v: String) {
        Log.i(TAG, "MapViewModel::setColorMode --> $v")
        val newMode: ColorMode =
            if (v.equals("Night", ignoreCase = true)) ColorMode.NIGHT else ColorMode.DAY
        val schema: IPresSchema? = presSchema
        if (schema is ColoredOSMPresSchema && schema.mode != newMode) {
            schema.mode = newMode
            repaintAsync()
        }
    }
    /**
     * Map Stuff
     */
    var geoList: List<GeoFeature> = mutableListOf()
    var presSchema: IPresSchema? = null
    val repo: IGeoFeatureRepository = OSMGeoPackageRepository(application)

    init {
        repo.initRepository(
            "Hagenberg-3857",
            "Gemeinde-Hagenberg-3857.gpkg",
            BoundingBox(
                1615309.0, 6168072.0,
                1616175.0, 6168652.0
            )
        )
        repo.openRepository("Hagenberg-3857")
        presSchema = ColoredOSMPresSchema(application)
        val layers: List<Triple<Int, String, String?>>? = presSchema?.getLayers()
        geoList = repo.fetchFeatures(layers)
    }

    private val poiRepository: PoiRepository = PoiRepository(application)

    /**
     * Tap-to-identify stuff
     */
    var selectedPoi: PoiFeature? by mutableStateOf(null)
        private set

    fun onMapTap(x: Float, y: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val pt = FloatArray(2)
            var best: PoiFeature? = null
            var bestDist: Float = TAP_RADIUS_PX * TAP_RADIUS_PX
            poiRepository.fetchPois().forEach { poi ->
                matrix.mapPoints(pt, floatArrayOf(poi.x, poi.y))
                val dx: Float = pt[0] - x
                val dy: Float = pt[1] - y
                val d2: Float = dx * dx + dy * dy
                if (d2 < bestDist) {
                    bestDist = d2
                    best = poi
                }
            }
            withContext(Dispatchers.Main) {
                selectedPoi = best
                repaintAsync()
            }
        }
    }

    fun clearSelection() {
        selectedPoi = null
        repaintAsync()
    }

    /**
     * Bitmap stuff
     */

    var bitmapA: Bitmap? = null
        private set

    var bitmapB: Bitmap? = null
        private set

    var useA: Boolean = true

    var matrix: Matrix = Matrix()
        private set

    var displayBitmap: ImageBitmap? by mutableStateOf<ImageBitmap?>(null)

    var world: Rect = Rect()
    var win: Rect = Rect()

    var width: Int  = 0
    var height: Int = 0

    fun resizeCanvas(w: Int, h: Int) {
        width = w
        height = h
        if (width > 0 && height > 0) {
            bitmapA = createBitmap(width, height)
            bitmapB = createBitmap(width, height)
                /*.apply {
                val canvas = Canvas(this)
                val p = Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 0f
                    style = Paint.Style.FILL_AND_STROKE
                    textSize = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,
                        24f,
                        Resources.getSystem().displayMetrics,
                    )
                }
                canvas.drawText("Bitmap", 100f, 100f, p)
            }

                 */
        }
    }

    fun getCanvasCentre(): Pair<Float, Float> {
        val centreX = width / 2f
        val centreY = height/ 2f
        return centreX to centreY
    }

    var scale: Float = 1f

    fun zoom(v: Float) {
        scale *= v
        val (cX, cY) = getCanvasCentre()
        matrix.postTranslate(-cX, -cY)
        matrix.postScale(v, v)
        matrix.postTranslate(cX, cY)
        repaintAsync()
    }

    fun scrollHorizontal(v: Float) {
        matrix.postTranslate(v, 0f)
        repaintAsync()
    }

    fun scrollVertical(v: Float)   {
        matrix.postTranslate(0f, v)
        repaintAsync()
    }

    fun zoomToFit() {
        win = Rect(0, 0, width, height)
        // world = geoList.getBBox()
        world = repo.getInitBBox()?.run {
            Rect(
                minLongitude.toInt(), minLatitude.toInt(),
                maxLongitude.toInt(), maxLatitude.toInt()
            )
        } ?: geoList.getBBox()

        val scaleX: Float = win.width().toFloat() / world.width().toFloat()
        val scaleY: Float = win.height().toFloat() / world.height().toFloat()
        scale = min(scaleX, scaleY) * 0.99f

        matrix.reset()
        matrix.postTranslate(-world.exactCenterX(), -world.exactCenterY())
        matrix.postScale(1f, -1f)
        matrix.postScale(scale, scale)
        matrix.postTranslate(win.exactCenterX(), win.exactCenterY())
        repaintAsync()
    }


    /**
     * Async stuff ...
     */

    private val drawingMutex: Mutex = Mutex()
    private var repaintJob: Job? = null

    private val trailPaint: Paint = Paint().apply {
        color = 0x9033B5E5.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 7f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val selectionPaint: Paint = Paint().apply {
        color = 0xFFE53935.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    fun repaintAsync() {
        repaintJob?.cancel()

        repaintJob = viewModelScope.launch {
            val newImage: Bitmap? = withContext(Dispatchers.IO) {
                repaint()
            }
            withContext(Dispatchers.Main) {
                newImage?.let {
                    displayBitmap = null
                    displayBitmap = it.asImageBitmap()
                }
            }
        }
    }

    private suspend  fun repaint() : Bitmap? {
        return drawingMutex.withLock {
            val workingBitmap: Bitmap? =  if (useA) bitmapA else bitmapB
            workingBitmap?.apply {
                val canvas: Canvas = Canvas(this)

                canvas.drawColor(presSchema?.getBackgroundColor() ?: Color.LTGRAY)

                canvas.save()
                // canvas.setMatrix(matrix)

                geoList.forEach { obj ->
                    if (!coroutineContext.isActive) return null

                    val tempPath: Path = Path(obj.geometry)
                    tempPath.transform(matrix)

                    val paints: List<Paint>? = presSchema?.getPresSchema(obj, scale)
                    paints?.forEach { p ->
                        canvas.drawPath(tempPath, p)
                    }
                } // geoList

                // breadcrumb trail below POIs and location marker
                synchronized(locList) {
                    if (trail.size >= 2) {
                        val trailPath: Path = Path()
                        val tPt: FloatArray = FloatArray(2)
                        trail.forEachIndexed { i, loc ->
                            matrix.mapPoints(tPt, floatArrayOf(loc.x, loc.y))
                            if (i == 0) {
                                trailPath.moveTo(tPt[0], tPt[1])
                            } else {
                                trailPath.lineTo(tPt[0], tPt[1])
                            }
                        }
                        canvas.drawPath(trailPath, trailPaint)
                    }
                }

                for (poi in poiRepository.fetchPois()) {
                    if (!coroutineContext.isActive) return null

                    var tempPoi: FloatArray = FloatArray(2)
                    matrix.mapPoints(tempPoi, floatArrayOf(poi.x, poi.y))
                    val drawX: Float = tempPoi[0] - poi.icon.width/2f
                    val drawY: Float = tempPoi[1] - poi.icon.height
                    canvas.drawBitmap(poi.icon, drawX, drawY, null)
                }

                // highlight ring around the tapped POI
                selectedPoi?.let { poi ->
                    val tempPoi: FloatArray = FloatArray(2)
                    matrix.mapPoints(tempPoi, floatArrayOf(poi.x, poi.y))
                    canvas.drawCircle(tempPoi[0], tempPoi[1] - poi.icon.height/2f, 48f, selectionPaint)
                }

                synchronized(locList) {
                    if (drawLocation) {
                        val paintFill = Paint().apply {
                            color = Color.BLUE
                        }
                        locList.forEach { loc ->
                            if (!coroutineContext.isActive) return null
                            val tCoord: FloatArray = FloatArray(2)
                            matrix.mapPoints(tCoord, floatArrayOf(loc.x,loc.y))
                            canvas.drawCircle(tCoord[0], tCoord[1], 12f, paintFill)
                        }
                    }
                }

                canvas.restore()
                useA = !useA
                /*
                val size= 25f
                val (cX, cY) = getCanvasCentre()

                canvas.drawLine(cX - size, cY, cX + size, cY, p)
                canvas.drawLine(cX, cY-size, cX, cY+size, p)
                 */
                // displayBitmap = asImageBitmap()

                workingBitmap
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        repo.closeRepository()
    }
}
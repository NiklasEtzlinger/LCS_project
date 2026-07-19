package at.fhooe.sail.cas.model.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.model.features.ContextFeature
import at.fhooe.sail.cas.model.features.ContextMetaData
import at.fhooe.sail.cas.model.features.ContextSituation
import at.fhooe.sail.cas.model.features.DummyFeature
import at.fhooe.sail.cas.model.features.LocationFeature
import at.fhooe.sail.cas.model.mediators.ContextSituationMediator
import at.fhooe.sail.cas.model.mediators.DummyFeatureMediator
import at.fhooe.sail.cas.model.mediators.LocationFeatureMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val ACTION_STOP_SERVICE = "KillMainService"
class MainService : Service(), IMainService, LocationListener {

    lateinit var locMgr: LocationManager

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var heartbeatJob: Job? = null

    inner class LocalBinder : Binder() {
        fun getService(): IMainService = this@MainService
    }

    private val binder = LocalBinder()

    /**
     * Lifecycle Stuff
     */

    override fun onCreate() {
        super.onCreate()
        locMgr = getSystemService(LocationManager::class.java)
        startAsForeground()
        startLocationUpdates()
        startWorkerThread()
    }

    override fun onDestroy() {
        super.onDestroy()
        heartbeatJob?.cancel()
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            heartbeatJob?.cancel()
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }
    /**
     * LocationListener Stuff
     */

    override fun onLocationChanged(loc: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            val mLoc: LocationFeature = LocationFeature(
                longitude = loc.longitude,
                latitude = loc.latitude
            )
            LocationFeatureMediator.emitData(mLoc)
        }
    }

    /**
     * API Stuff
     */
    override fun someApiMethod() {
        Log.i(TAG, "MainService::someApiMethod() ... ")
    }

    val ceBlackboard: MutableMap<String, ContextFeature> = mutableMapOf()

    override fun broadcastContextFeatures(values: List<ContextFeature>) {
        CoroutineScope(Dispatchers.IO).launch {
            values.forEach { v ->
                /** analyze context features */
                Log.i(TAG, "MainService::broadcastContextFeature() -> $v")
                ceBlackboard.put(v.id, v)
            }
            val situation: ContextSituation = ContextSituation(
                ContextMetaData(),
                ceBlackboard.values.toList()
            )
            ContextSituationMediator.emitData(situation)
        }
    }

    /**
     * Private Stuff
     */

    private fun startAsForeground() {
        val channelId: String = "MainServiceChannelId"
        val channel = NotificationChannel(
            channelId, "CAS-Service", NotificationManager.IMPORTANCE_HIGH
        )
        val nMgr: NotificationManager = getSystemService(NotificationManager::class.java)
        nMgr.createNotificationChannel(channel)

        val stopIntent = Intent(this, MainService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }

        val stopPendingIntent: PendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val na: Notification.Action = Notification.Action.Builder(
            Icon.createWithResource(
                this,
                android.R.drawable.ic_menu_close_clear_cancel),
            getString(android.R.string.cancel),
            stopPendingIntent
        ).build()

        val n: Notification = Notification.Builder(this, channelId)
            .setContentTitle("CAS Service active")
            .setContentText("Foreground-Service running in the background")
            .setSmallIcon(R.drawable.ic_service_24dp)
            .setColor(Color.RED)
            .setColorized(true)
            .addAction(na)
            .build()

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED) {
            startForeground(42, n)
        }
    }
    private fun startWorkerThread() {
        heartbeatJob = serviceScope.launch {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH:mm:ss")
            while (isActive) {
                Log.i(TAG, "ping!")
                delay(5000)
                val timestamp: String = LocalDateTime.now().format(formatter)
                DummyFeatureMediator.emitData(DummyFeature("$timestamp some info"))
            }
        }
    }

    private fun startLocationUpdates() {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            locMgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                5f,
                this
            )
        }
    }
}
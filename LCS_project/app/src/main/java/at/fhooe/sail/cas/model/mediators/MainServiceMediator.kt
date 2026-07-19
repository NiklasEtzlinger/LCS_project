package at.fhooe.sail.cas.model.mediators

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import at.fhooe.sail.cas.model.service.IMainService
import at.fhooe.sail.cas.model.service.MainService

object MainServiceMediator {

    private var boundService: IMainService? = null
    private var appContext: Application? = null

    fun getInstance(): IMainService? = boundService

    val serviceConnection: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder: MainService.LocalBinder? = service as? MainService.LocalBinder
            boundService = binder?.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
        }
    }

    fun bind(context: Application) {
        appContext = context
        val i: Intent = Intent(context, MainService::class.java)
        appContext?.startForegroundService(i)
        appContext?.bindService(
            i,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun unbind() {
        boundService?.apply {
            appContext?.unbindService(serviceConnection)
            boundService = null
        }
    }


}
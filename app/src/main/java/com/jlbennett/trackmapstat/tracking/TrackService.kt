package com.jlbennett.trackmapstat.tracking

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.util.Log
import androidx.lifecycle.LifecycleObserver

class TrackService : Service(), LifecycleObserver {

    private val binder: IBinder = TrackBinder()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: TrackLocationListener

    override fun onCreate() {
        super.onCreate()
        Log.d("TrackService", "Service onCreate()")
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = TrackLocationListener()
    }

    override fun onBind(intent: Intent?): IBinder? {
        //Start notification here.
        //Consider onStart() if Service is started with startService(), not bindService()
        //Maybe have a separate function for the notification. Which is then called from both?
        return binder
    }

    fun startTracking() {
        Log.d("TrackService", "startTracking - inService")
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 1F, locationListener)
        } catch (exception: SecurityException) {
            Log.d("TrackLogs", "TrackService - SecurityException $exception")
        }
    }

    fun stopTracking() {
        locationManager.removeUpdates(locationListener)
    }

    inner class TrackBinder : Binder(), IInterface {
        override fun asBinder(): IBinder {
            return this
        }

        fun getService(): TrackService {
            return this@TrackService
        }

    }

}
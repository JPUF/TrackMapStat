package com.jlbennett.trackmapstat.tracking

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.RemoteCallbackList
import android.util.Log
import androidx.lifecycle.LifecycleObserver

class TrackService : Service(), LifecycleObserver {

    private val binder: IBinder = TrackBinder()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: TrackLocationListener
    val remoteCallbackList = RemoteCallbackList<TrackBinder>()

    override fun onCreate() {
        super.onCreate()
        Log.d("TrackService", "Service onCreate()")
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = TrackLocationListener(this)
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

    fun executeCallbacks(location: Location) {
        //TODO update and increment all elements of the run (polyline, etc) as a member of this service.
        //So the run exists outside of the fragment's lifecycle.
        val callbackCount = remoteCallbackList.beginBroadcast()
        for(i in 0 until callbackCount) {
            remoteCallbackList.getBroadcastItem(i).callback!!.onLocationUpdate(location)
        }
        remoteCallbackList.finishBroadcast()
    }

    inner class TrackBinder : Binder(), IInterface {

        var callback: ITrackCallback? = null

        override fun asBinder(): IBinder {
            return this
        }

        fun getService(): TrackService {
            return this@TrackService
        }

        fun registerCallback(callback: ITrackCallback) {
            this.callback = callback
            remoteCallbackList.register(binder as TrackBinder)
        }
        fun unregisterCallback(callback: ITrackCallback) {
            remoteCallbackList.unregister(binder as TrackBinder)
        }

    }

    interface ITrackCallback {
        fun onLocationUpdate(location: Location)
    }

}
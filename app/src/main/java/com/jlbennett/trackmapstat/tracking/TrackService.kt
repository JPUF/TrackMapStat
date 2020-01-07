package com.jlbennett.trackmapstat.tracking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import com.jlbennett.trackmapstat.R

//Service provides data, and is considered part of Model in MVVM
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
        Log.d("TrackService", "service onBind")
        //Start notification here.
        showNotification()
        return binder
    }

    private fun showNotification() {
        val channelID = "100"
        val notificationID = 1
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationName = "TrackStatMap Notification Channel"
            val description = "Notification Channel for TrackStatMap app"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelID, notificationName, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }
        val trackIntent = Intent(this, TrackFragment::class.java)
        val trackPendingIntent = PendingIntent.getActivity(this, 0, trackIntent, 0)
        //TODO notification doesn't return user to Fragment.
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_runner)
            .setSound(null)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText("Return to Map")
            .setContentIntent(trackPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(notificationID, builder.build())
    }

    override fun onDestroy() {
        Log.d("TrackService", "Service onDestroy()")
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("TrackService", "Service onUnbind()")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackService", "Service onStartCommand()")
        return START_STICKY
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
        Log.d("TrackService", "stopTracking - removing updates")
        locationManager.removeUpdates(locationListener)
        stopSelf()
    }

    fun executeCallbacks(location: Location) {
        //TODO update and increment all elements of the run (polyline, etc) as a member of this service.
        //So the run exists outside of the fragment's lifecycle.
        val callbackCount = remoteCallbackList.beginBroadcast()
        for (i in 0 until callbackCount) {
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
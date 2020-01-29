package com.jlbennett.trackmapstat.tracking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.jlbennett.trackmapstat.MainActivity
import com.jlbennett.trackmapstat.R
import com.jlbennett.trackmapstat.Run

/*
    Service provides data, and is considered part of the Model in MVVM
    It has a separate lifecycle to the View, and continues executing even after the relevant Fragment is inactive.
 */
class TrackService : Service(), LifecycleObserver {

    private val binder: IBinder = TrackBinder()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: TrackLocationListener
    private var notificationManager: NotificationManager? = null
    val remoteCallbackList = RemoteCallbackList<TrackBinder>()
    private lateinit var wakeLock: PowerManager.WakeLock

    //The service has a Run object, which is updated as the user location changes.
    private val run = Run(null, 0F, 0L, 0L, null, PolylineOptions(), false)

    override fun onCreate() {
        super.onCreate()
        Log.d("TrackService", "Service onCreate()")

        //The user's current location is obtained using the LOCATION_SERVICE system service.
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = TrackLocationListener(this)
        wakeLock = (application.getSystemService(Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TrackMapStat::TrackWakelock")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("TrackService", "service onBind")
        return binder
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
        wakeLock.acquire(5*60*60*1000L)//5 Hour timeout.
        Log.d("TrackService", "acquired wake lock ----------------------------------------------")
        Log.d("TrackService", "startTracking - inService")
        try {
            //Begin tracking location updates from the LocationListener
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1L, 1F, locationListener)
        } catch (exception: SecurityException) {
            Log.d("TrackLogs", "TrackService - SecurityException $exception")
        }
        showNotification()
    }

    fun stopTracking() {
        wakeLock.release()
        Log.d("TrackService", "released wake lock ----------------------------------------------")
        locationManager.removeUpdates(locationListener)//Stop receiving updates from LocationListener
        notificationManager!!.cancelAll()
        executeFinishCallback(run)
        stopSelf()
        Log.d("TrackService", "stopTracking - run: ${run.distance}m")
    }

    /*
        Executes the implemented interface location callback method. This is how data is passed from the Service.
     */
    private fun executeLocationCallback(run: Run) {
        val callbackCount = remoteCallbackList.beginBroadcast()
        for (i in 0 until callbackCount) {
            remoteCallbackList.getBroadcastItem(i).callback!!.onLocationUpdate(run)
        }
        remoteCallbackList.finishBroadcast()
    }

    private fun executeFinishCallback(run: Run) {
        val callbackCount = remoteCallbackList.beginBroadcast()
        for (i in 0 until callbackCount) {
            remoteCallbackList.getBroadcastItem(i).callback!!.onRunFinished(run)
        }
        remoteCallbackList.finishBroadcast()
    }

    /*
        The function that is called by the LocationListener. This then updates the Service's local Run object.
     */
    fun updateRun(location: Location) {
        if (run.latestLocation == null || run.timeStarted == null) {
            run.latestLocation = location
            run.timeStarted = System.currentTimeMillis() * 1000000
            run.runStarted = true
        }
        run.distance += location.distanceTo(run.latestLocation)
        run.timeElapsed = (System.currentTimeMillis() * 1000000) - run.timeStarted!!
        run.routeLine.add(LatLng(location.latitude, location.longitude)).color(Color.parseColor("#731086")).width(12F)
        Log.d("TrackService", "Run: ${run.distance}m : ${run.timeElapsed / 1000000}")
        run.latestLocation = location
        executeLocationCallback(run)
    }

    /*
        Displays a nice little notification for the user.
     */
    private fun showNotification() {
        val channelID = "100"
        val notificationID = 1
        //The appropriate System Service is acquired.
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Additional data fields are included to support later SDK versions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationName = "TrackStatMap Notification Channel"
            val description = "Notification Channel for TrackStatMap app"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelID, notificationName, importance)
            channel.description = description
            notificationManager!!.createNotificationChannel(channel)
        }

        //An intent is included that is supposed to return the user back to the TrackFragment
        val trackIntent = Intent(applicationContext, MainActivity::class.java)
        trackIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val trackPendingIntent =
            PendingIntent.getActivity(applicationContext, 1001, trackIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.ic_runner)
            .setSound(null)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText("Your location is being tracked")
            .setContentIntent(trackPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        notificationManager!!.notify(notificationID, builder.build())
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
    }

    interface ITrackCallback {
        fun onLocationUpdate(run: Run)

        fun onRunFinished(run: Run)
    }

}
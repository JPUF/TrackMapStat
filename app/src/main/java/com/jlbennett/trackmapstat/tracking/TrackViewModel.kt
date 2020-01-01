package com.jlbennett.trackmapstat.tracking

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private var locationManager: LocationManager
    private var locationListener: TrackingLocationListener

    init {
        Log.d("TrackLogs", "TrackViewModel init: Created!")
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = TrackingLocationListener()
        readLocation()
    }

    private fun readLocation() {
        Log.d("TrackLogs", "readLocation()")
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 5F, locationListener)
        } catch (exception: SecurityException) {
            Log.d("TrackLogs", "$exception")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
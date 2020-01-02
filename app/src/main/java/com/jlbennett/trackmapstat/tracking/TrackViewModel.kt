package com.jlbennett.trackmapstat.tracking

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private var locationManager: LocationManager
    private var locationListener: TrackingLocationListener
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation


    init {
        Log.d("TrackLogs", "TrackViewModel init: Created!")
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = TrackingLocationListener(this)
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 5F, locationListener)
        } catch (exception: SecurityException) {
            Log.d("TrackLogs", "$exception")
        }
    }

    fun updateLocation(location: Location) {
        _currentLocation.value = location
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
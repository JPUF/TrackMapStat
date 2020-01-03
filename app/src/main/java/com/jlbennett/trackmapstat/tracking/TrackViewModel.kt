package com.jlbennett.trackmapstat.tracking

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private var locationManager: LocationManager
    private var locationListener: TrackingLocationListener
    private val _currentLocation = MutableLiveData<Location>()
    private var distanceTally: Float = 0F
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    private val _currentDistance = MutableLiveData<Float>(0F)
    val currentDistance: LiveData<Float>
        get() = _currentDistance

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
        /*_currentDistance.value.let {distance ->
            _currentDistance.value = distance!! + location.distanceTo(_currentLocation.value)
        }*/
        if(_currentLocation.value == null) {
            _currentLocation.value = location
        }
        distanceTally += location.distanceTo(_currentLocation.value)
        _currentDistance.value = distanceTally
        _currentLocation.value = location
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
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
    private var tracking: Boolean = false
    private var distanceTally: Float = 0F
    private var timeStarted: Long = 0L

    val currentLocation: LiveData<Location>
        get() = _currentLocation

    private val _currentDistance = MutableLiveData<Float>(0F)
    val currentDistance: LiveData<Float>
        get() = _currentDistance

    private val _currentTime = MutableLiveData<Long>(0L)
    val currentTime: LiveData<Long>
        get() = _currentTime

    init {
        Log.d("TrackLogs", "TrackViewModel init: Created!")
        locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = TrackingLocationListener(this)
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 1F, locationListener)
        } catch (exception: SecurityException) {
            Log.d("TrackLogs", "$exception")
        }
    }

    fun updateLocation(location: Location) {
        if(tracking){
            if (_currentLocation.value == null) {
                _currentLocation.value = location
                timeStarted = location.elapsedRealtimeNanos
                timeStarted = System.currentTimeMillis() * 1000000
            }
            distanceTally += location.distanceTo(_currentLocation.value)

            _currentDistance.value = distanceTally
            _currentTime.value = (System.currentTimeMillis() * 1000000) - timeStarted

            _currentLocation.value = location
        }
    }

    fun startTracking() {
        tracking = true
    }

    fun stopTracking() {
        tracking = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
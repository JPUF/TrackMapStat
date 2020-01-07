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
    }

    fun updateLocation(location: Location) {
        if (tracking) {
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

    fun startTracking() {//maybe request location updates from here.
        tracking = true
    }

    fun stopTracking() {//maybe remove updates from here.
        tracking = false
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
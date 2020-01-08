package com.jlbennett.trackmapstat.tracking

import android.app.Application
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentLocation = MutableLiveData<Location>()
    private var distanceTally: Float = 0F
    private var timeStarted: Long = 0L
    private var routeLine = PolylineOptions()


    private val _currentDistance = MutableLiveData<Float>(0F)
    val currentDistance: LiveData<Float>
        get() = _currentDistance

    private val _currentTime = MutableLiveData<Long>(0L)
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val _currentLine = MutableLiveData<PolylineOptions>()
    val currentLine: LiveData<PolylineOptions>
        get() = _currentLine

    //TODO extract this data into a Run model class.
    //get() = Run.distance
    //maybe
    //TODO all Run info needs to persist beyond fragment lifecycle.

    init {
        Log.d("TrackLogs", "TrackViewModel init: Created!")
    }

    fun updateLocation(location: Location) {
        if (_currentLocation.value == null) {
            _currentLocation.value = location
            timeStarted = location.elapsedRealtimeNanos
            timeStarted = System.currentTimeMillis() * 1000000
        }
        distanceTally += location.distanceTo(_currentLocation.value)

        _currentDistance.value = distanceTally
        _currentTime.value = (System.currentTimeMillis() * 1000000) - timeStarted

        _currentLocation.value = location

        routeLine.add(LatLng(location.latitude, location.longitude)).color(Color.RED).width(12F)
        _currentLine.value = routeLine
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
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
import com.jlbennett.trackmapstat.Run

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentDistance = MutableLiveData<Float>(0F)
    val currentDistance: LiveData<Float>
        get() = _currentDistance

    private val _currentTime = MutableLiveData<Long>(0L)
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val _currentLine = MutableLiveData<PolylineOptions>()
    val currentLine: LiveData<PolylineOptions>
        get() = _currentLine

    private val _runStarted = MutableLiveData<Boolean>()
    val runStarted: LiveData<Boolean>
        get() = _runStarted

    //TODO extract this data into a Run model class.
    //get() = Run.distance
    //maybe
    //TODO all Run info needs to persist beyond fragment lifecycle.

    init {
        Log.d("TrackLogs", "TrackViewModel init: Created!")
    }

    fun updateRun(run: Run) {
        _currentDistance.value = run.distance
        _currentTime.value = run.timeElapsed
        _currentLine.value = run.routeLine
        _runStarted.value = run.runStarted
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TrackLogs", "TrackViewModel: onCleared()")
    }
}
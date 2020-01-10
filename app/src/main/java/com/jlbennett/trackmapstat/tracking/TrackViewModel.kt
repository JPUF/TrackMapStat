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
/*
    The TrackingFragment's ViewModel. This holds the dynamic data elements that are presented by the TrackFragment.
    The ViewModel has no reference to the View, thus ensuring that class coupling remains very loose.
 */
class TrackViewModel(application: Application) : AndroidViewModel(application) {

    //Each dynamic data object is stored as LiveData. This allows for data changes to be 'Observed' in the Fragment.
    //The private MutableLiveData allows is what is acted upon within this class.
    //This is then exposed to the View as LiveData, ensuring the View has read-only access.
    private val _currentDistance = MutableLiveData<Float>(0F)
    val currentDistance: LiveData<Float>
        get() = _currentDistance

    private val _currentTime = MutableLiveData<Long>(0L)
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val _currentLine = MutableLiveData<PolylineOptions>()
    val currentLine: LiveData<PolylineOptions>
        get() = _currentLine

    var runStarted: Boolean = false

    /*
        To be called on each location update. The ViewModel's fields are kept up to date.
     */
    fun updateRun(run: Run) {
        _currentDistance.value = run.distance
        _currentTime.value = run.timeElapsed
        _currentLine.value = run.routeLine
        runStarted = run.runStarted
        Log.d("TrackButton", "ViewModel: $runStarted")
    }
}
package com.jlbennett.trackmapstat

import android.location.Location
import com.google.android.gms.maps.model.PolylineOptions

class Run {
    var distance: Float = 0F
    var timeElapsed: Long = 0L
    var timeStarted: Long? = null
    var latestLocation: Location? = null
    val routeLine = PolylineOptions()
    var runStarted: Boolean = false
}
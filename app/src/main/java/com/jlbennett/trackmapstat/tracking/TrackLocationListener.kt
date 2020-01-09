package com.jlbennett.trackmapstat.tracking

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log

class TrackLocationListener(private val trackService: TrackService) : LocationListener {

    override fun onLocationChanged(location: Location?) {
        Log.d("TrackService", "Location = ${location!!.latitude}:${location.longitude}")
        trackService.updateRun(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d("TrackService", "onStatusChanged: $provider : $status")
    }

    override fun onProviderEnabled(provider: String?) {
        Log.d("TrackService","onProviderEnabled: $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("TrackService", "onProviderDisabled: $provider")
    }
}
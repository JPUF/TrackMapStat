package com.jlbennett.trackmapstat

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log

class TrackingLocationListener : LocationListener {

    override fun onLocationChanged(location: Location?) {
        Log.d("TrackLogs", "Location = ${location!!.latitude}:${location.longitude}")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d("TrackLogs", "onStatusChanged: $provider : $status")
    }

    override fun onProviderEnabled(provider: String?) {
        Log.d("TrackLogs","onProviderEnabled: $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("TrackLogs", "onProviderDisabled: $provider")
    }
}
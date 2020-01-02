package com.jlbennett.trackmapstat.tracking

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log

class TrackingLocationListener(private val viewModel: TrackViewModel) : LocationListener {

    override fun onLocationChanged(location: Location?) {
        Log.d("TrackLogs", "Location = ${location!!.latitude}:${location.longitude}")
        viewModel.updateLocation(location)
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
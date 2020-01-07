package com.jlbennett.trackmapstat.tracking

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log

class TrackLocationListener : LocationListener {

    override fun onLocationChanged(location: Location?) {
        Log.d("TrackService", "new Location = ${location!!.latitude}:${location.longitude}")
        //iewModel.updateLocation(location)
        //TODO update via Callback?
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
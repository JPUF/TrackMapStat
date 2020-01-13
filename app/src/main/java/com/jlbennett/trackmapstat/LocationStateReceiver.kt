package com.jlbennett.trackmapstat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.widget.Toast

/*
    A Broadcast receiver to remind users to re-enable their GPS, if they've turned it off.
 */
class LocationStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!locationEnabled) {
                Toast.makeText(context, "Please turn location back on", Toast.LENGTH_LONG).show()
            }
        }
    }
}

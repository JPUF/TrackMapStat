package com.jlbennett.trackmapstat

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jlbennett.trackmapstat.databinding.FragmentTrackBinding


class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)

        val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = TrackingLocationListener()
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5L, 5F, locationListener)
        } catch (exception: SecurityException) {
            Log.d("TrackLogs", "$exception")
        }

        return binding.root
    }
}

package com.jlbennett.trackmapstat.tracking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.jlbennett.trackmapstat.R
import com.jlbennett.trackmapstat.databinding.FragmentTrackBinding


class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)

        val viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            //TODO handle permission granting
        })
        viewModel.currentLocation.observe(this, Observer {location ->
            Log.d("TrackLogs", "Location in Fragment: ${location.latitude} : ${location.longitude}")
            val localLatLng = LatLng(location.latitude, location.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localLatLng, 16F))

        })

        return binding.root
    }
}

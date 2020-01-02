package com.jlbennett.trackmapstat.tracking

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.jlbennett.trackmapstat.R
import com.jlbennett.trackmapstat.databinding.FragmentTrackBinding


class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private var routeLine = PolylineOptions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)

        val viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            try{
                googleMap.isMyLocationEnabled = true
            } catch (exception: SecurityException) {
                //TODO handle permission granting
            }
        })
        viewModel.currentLocation.observe(this, Observer { newLocation ->
            Log.d("TrackLogs", "Location in Fragment: ${newLocation.latitude} : ${newLocation.longitude}")
            val localLatLng = LatLng(newLocation.latitude, newLocation.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localLatLng, 17F))
            googleMap.addPolyline(
                routeLine.add(LatLng(newLocation.latitude, newLocation.longitude)).color(Color.RED).width(20F)
            )

        })

        return binding.root
    }
}

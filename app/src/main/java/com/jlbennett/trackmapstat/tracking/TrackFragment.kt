package com.jlbennett.trackmapstat.tracking

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
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
        mapFragment.getMapAsync { map ->
            googleMap = map
            try{
                googleMap.isMyLocationEnabled = true
            } catch (exception: SecurityException) {
                //TODO handle permission granting
            }
        }
        viewModel.currentLocation.observe(this, Observer { newLocation ->
            Log.d("TrackLogs", "Location in Fragment: ${newLocation.latitude} : ${newLocation.longitude}")
            val localLatLng = LatLng(newLocation.latitude, newLocation.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(localLatLng, 17F))//move above
            googleMap.addPolyline(
                routeLine.add(LatLng(newLocation.latitude, newLocation.longitude)).color(Color.RED).width(12F).endCap(RoundCap())
            )
        })

        viewModel.currentDistance.observe(this, Observer {distance ->
            binding.distanceText.text = "${"%.2f".format(distance)}m"
        })

        viewModel.currentTime.observe(this, Observer {time ->
            val seconds = (time / 1000000000)
            val minutes = seconds / 60
            val hours = minutes / 60
            binding.timeText.text = "%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
        })

        binding.startStopButton.setOnClickListener {buttonView ->
            val button = buttonView as Button
            when(button.tag) {
                "start" -> {
                    viewModel.startTracking()
                    button.tag = "stop"
                    button.text = resources.getString(R.string.stop)
                    button.setBackgroundColor(Color.RED)
                }
                else -> {
                    viewModel.stopTracking()
                    button.tag = "start"
                    button.text = resources.getString(R.string.start)
                    button.setBackgroundColor(Color.GREEN)
                    Toast.makeText( context, "Run stopped - SAVE NOW", Toast.LENGTH_LONG).show()
                    fragmentManager!!.popBackStack()
                }
            }
        }

        return binding.root
    }
}

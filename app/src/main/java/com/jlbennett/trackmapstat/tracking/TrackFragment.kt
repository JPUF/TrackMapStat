package com.jlbennett.trackmapstat.tracking

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.IBinder
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.jlbennett.trackmapstat.R
import com.jlbennett.trackmapstat.Run
import com.jlbennett.trackmapstat.databinding.FragmentTrackBinding


class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var viewModel: TrackViewModel
    var service: TrackService? = null
    var isServiceBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as TrackService.TrackBinder
            service = binder.getService()
            isServiceBound = true
            binder.registerCallback(callback)
            Log.d("TrackService", "onServiceConnected - service is null: ${service == null}")
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            isServiceBound = false
            service = null
        }
    }

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private val serviceClass = TrackService::class.java
    private lateinit var serviceIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)

        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        serviceIntent = Intent(activity, serviceClass)

        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        if (!::googleMap.isInitialized) initMap()
        mapView.onResume()

        viewModel.currentLine.observe(this, Observer { line ->
            if (!::googleMap.isInitialized) {
                Log.d("TrackService", "Map not initialised: calling initMap")
                initMap(line)
            } else {
                updateLine(line)
            }
        })

        viewModel.currentDistance.observe(this, Observer { distance ->
            binding.distanceText.text = "${"%.2f".format(distance)}m"
        })

        viewModel.currentTime.observe(this, Observer { time ->
            val seconds = (time / 1000000000)
            val minutes = seconds / 60
            val hours = minutes / 60
            binding.timeText.text = "%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
        })

        binding.startStopButton.setOnClickListener { buttonView ->
            //TODO button should be formatted based off LiveData: runStarted
            val button = buttonView as Button
            when (button.tag) {
                "start" -> {
                    button.tag = "stop"
                    button.text = resources.getString(R.string.stop)
                    button.setBackgroundColor(Color.RED)
                    service!!.startTracking()
                }
                else -> {
                    service!!.stopTracking()
                    button.tag = "start"
                    button.text = resources.getString(R.string.start)
                    button.setBackgroundColor(Color.GREEN)
                    Toast.makeText(context, "Run stopped - SAVE NOW", Toast.LENGTH_LONG).show()
                    fragmentManager!!.popBackStack()
                }
            }
        }

        return binding.root
    }

    val callback = object : TrackService.ITrackCallback {
        override fun onLocationUpdate(run: Run) {
            viewModel.updateRun(run)
        }

    }

    private fun updateLine(line: PolylineOptions) {
        val latestLocation = line.points[line.points.size - 1]
        val latestLatLng = LatLng(latestLocation.latitude, latestLocation.longitude)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latestLatLng, 17F))
        googleMap.addPolyline(line)
        //TODO should add all points that are yet to have been drawn. Avoid skipping
    }

    private fun isTrackServiceRunning(): Boolean {
        val activityManager: ActivityManager =
            activity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.className == TrackService::class.java.name) {
                return true
            }
        }
        return false
    }

    private fun initMap(line: PolylineOptions? = null) {
        mapView.getMapAsync { map ->
            googleMap = map
            val initialPosition = LatLng(52.95, -1.15)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10F))
            try {
                googleMap.isMyLocationEnabled = true
            } catch (exception: SecurityException) {
                //TODO handle permission granting
            }
            if (line != null) {
                updateLine(line)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTrackServiceRunning()) {//TODO can remove this check and method.
            //service already running
            Log.d("TrackService", "starting and binding - service already running")
        } else {
            //Start service afresh
            Log.d("TrackService", "starting and binding - service wasn't started")
        }
        activity!!.startService(serviceIntent)
        activity!!.bindService(serviceIntent, connection, 0)
    }

    override fun onPause() {
        super.onPause()
        if (isServiceBound) {
            activity!!.unbindService(connection)
        }
    }
}

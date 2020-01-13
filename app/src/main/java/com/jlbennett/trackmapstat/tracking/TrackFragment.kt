package com.jlbennett.trackmapstat.tracking

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.jlbennett.trackmapstat.R
import com.jlbennett.trackmapstat.Run
import com.jlbennett.trackmapstat.databinding.FragmentTrackBinding

/*
    This Fragment is used for the UI functionality to allow the user to track their run.
    The tracking functionality is the most complex part of the system, so the MVVM architecture was employed.
    This Fragment represents part of the 'View', alongside the layout XML.
    It is also responsible for starting and handling the underlying Service.
 */
class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding
    private lateinit var viewModel: TrackViewModel//View has a reference to its ViewModel
    var service: TrackService? = null
    var isServiceBound = false

    private val connection = object : ServiceConnection {

        //Asynchronous callback used to setup local references to the active Service.
        override fun onServiceConnected(className: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as TrackService.TrackBinder
            service = binder.getService()
            isServiceBound = true
            binder.registerCallback(callback)
            Log.d("TrackService", "onServiceConnected")
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            isServiceBound = false
            service = null
        }
    }

    //Class-wide reference to both a GoogleMap instance, and the MapView which contains and displays it.
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private val serviceClass = TrackService::class.java
    private lateinit var serviceIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false)

        //The viewModel is always initialised to the same (Singleton) instance of the ViewModel.
        //So the same data will be presented throughout configuration changes, or other lifecycle events.
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        serviceIntent = Intent(activity, serviceClass)

        mapView = binding.map
        //To allow for persistence beyond Fragment lifecycle.
        mapView.onCreate(savedInstanceState)
        if (!::googleMap.isInitialized) initMap()
        mapView.onResume()

        //The ViewModel exposes its data as LiveData objects. These are then 'Observed' here in the View.
        //Every time the ViewModel currentLine object is updated, this callback is executed.
        viewModel.currentLine.observe(this, Observer { line ->
            if (!::googleMap.isInitialized) {
                Log.d("TrackService", "Map not initialised: calling initMap")
                initMap(line)
            } else {
                updateLine(line)
            }
        })

        //The distance TextView is updated each time the ViewModel's currentDistance object is updated.
        viewModel.currentDistance.observe(this, Observer { distance ->
            binding.distanceText.text = "${"%.2f".format(distance)}m"
        })

        //The time TextView is updated each time the ViewModel's currentTime object is updated.
        viewModel.currentTime.observe(this, Observer { time ->
            val seconds = (time / 1000000000)
            val minutes = seconds / 60
            val hours = minutes / 60
            binding.timeText.text = "%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
        })

        formatButton()

        binding.startStopButton.setOnClickListener {
            formatButton()
            when (viewModel.runStarted) {
                true -> {
                    service!!.stopTracking()//Called when the user has finished their run.
                }
                false -> {
                    service!!.startTracking()//Called when the user has just started their run.
                }
            }
        }

        return binding.root
    }

    /*
        Ensures the button correctly displays depending on the state of tracking.
     */
    private fun formatButton() {
        val button = binding.startStopButton
        Log.d("TrackButton", "Formatting Button - has run started? ${viewModel.runStarted}")
        when(viewModel.runStarted) {
            true -> {
                button.text = "STOP"
                button.setBackgroundColor(Color.RED)
            }
            false -> {
                button.text = "START"
            }
        }
    }

    private fun navigateToSaveFragment(run: Run) {
        Log.d("TrackService", "navigate with run: ${run.distance}")
        //Navigation Component used again. This time with an argument. Instead of sending data in a bundle.
        //The run info is sent using 'SafeArgs', which ensures type safety on receipt.
        //The SafeArgs plug-in generates this new class.
        findNavController().navigate(TrackFragmentDirections.actionTrackFragmentToSaveRunFragment(run))
    }


    //TODO could this be in the ViewModel?
    /*
        Implements the Service's callback interface. Acts as mid-ground between the Service and ViewModel.
     */
    val callback = object : TrackService.ITrackCallback {
        override fun onLocationUpdate(run: Run) {
            viewModel.updateRun(run)//Updates state stored in the ViewModel. Called each location update.
            formatButton()
        }

        override fun onRunFinished(run: Run) {
            navigateToSaveFragment(run)
        }
    }

    /*
        Add Polyline to the map.
     */
    private fun updateLine(line: PolylineOptions) {
        val latestLocation = line.points[line.points.size - 1]
        val latestLatLng = LatLng(latestLocation.latitude, latestLocation.longitude)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latestLatLng, 17F))
        googleMap.addPolyline(line)
    }

    /*
        Handles the GoogleMap API getMapAsync callback.
     */
    private fun initMap(line: PolylineOptions? = null) {
        mapView.getMapAsync { map ->
            googleMap = map//Set local reference to the Map from the API call.
            val initialPosition = LatLng(52.95, -1.15)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10F))//Zoom to initial location.
            try {
                googleMap.isMyLocationEnabled = true//Allow user to view their current location, before tracking begins.
            } catch (exception: SecurityException) {
                binding.permissionText.visibility = View.VISIBLE
            }
            if (line != null) {//True when the map is reinitialised after tracking has already begun
                updateLine(line)
            }
        }
    }

    /*
        Handle Fragment lifecycle events
     */
    override fun onResume() {
        super.onResume()
        activity!!.startService(serviceIntent)
        activity!!.bindService(serviceIntent, connection, 0)
        //Each time the fragment is opened and active, the Service is bound to.
        //The Service is a Singleton, so even if it has already been started, the same service is bound to again.
    }

    override fun onPause() {
        super.onPause()
        if (isServiceBound) {
            activity!!.unbindService(connection)
        }
        //Service unbound whenever the Fragment is no longer active.
    }
}

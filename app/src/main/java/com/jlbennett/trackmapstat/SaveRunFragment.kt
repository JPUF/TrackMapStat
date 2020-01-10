package com.jlbennett.trackmapstat


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.jlbennett.trackmapstat.database.RunContract
import com.jlbennett.trackmapstat.databinding.FragmentSaveRunBinding
import com.jlbennett.trackmapstat.database.RunContract.RunEntry

/*
    A fragment that the user is directed to once they've finished a run.
    It is responsible for displaying an overview of their recent run
    and allowing the user to name and save it to the DB.
 */
class SaveRunFragment : Fragment() {

    private lateinit var binding: FragmentSaveRunBinding

    //This Fragment can only be navigated to if a Run object is passed as a 'SafeArg', as defined in navigation.xml
    private val args: SaveRunFragmentArgs by navArgs()
    private lateinit var run: Run
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_save_run, container, false)
        run = args.run//The Run object is obtained from the SafeArgs
        Log.d("TrackSaveRun", "Run: distance = ${run.distance}")

        //The UI fields are updated to display an overview of the run.
        binding.distanceText.text = "Distance: ${"%.2f".format(run.distance)}m"
        val seconds = (run.timeElapsed / 1000000000)
        val minutes = seconds / 60
        val hours = minutes / 60
        binding.timeText.text = "Time: ${"%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)}"

        mapView = binding.overviewMap
        mapView.onCreate(savedInstanceState)
        if (!::googleMap.isInitialized) initMap(run.routeLine)
        mapView.onResume()

        binding.saveButton.setOnClickListener { saveRun() }

        return binding.root
    }

    /*
        Display a GoogleMap in a MapView, to show the user's recent run.
     */
    private fun initMap(line: PolylineOptions) {
        mapView.getMapAsync { map ->
            googleMap = map
            val boundsBuilder = LatLngBounds.Builder()
            for(point: LatLng in line.points){
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()//This are the limiting bounds of the line.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))//This displays only the run area.
            googleMap.addPolyline(line)
        }
    }


    /*
        Save the run information to the DB, via the ContentProvider.
     */
    private fun saveRun() {
        val name: String = binding.nameEntry.text.toString()
        val distance: Float = run.distance
        val time: Float = run.timeElapsed.toFloat()

        val runValues = ContentValues()
        runValues.put(RunEntry.COLUMN_NAME, name)
        runValues.put(RunEntry.COLUMN_DISTANCE, distance)
        runValues.put(RunEntry.COLUMN_TIME, time)

        //The run must be named by the user in order to be saved.
        if (name.isNotBlank()) {
            //The ContentProvider's insert is called, with the Run's values.
            context!!.contentResolver.insert(RunContract.CONTENT_URI, runValues)

            //Confirmation is given to the user by displaying a Toast.
            Toast.makeText(context, "Run: '$name' has been saved", Toast.LENGTH_LONG).show()
            //This navigates the user back to the home page (as defined in navigation.xml)
            fragmentManager!!.popBackStack()
        } else {
            //If they failed to name the run, they are made aware of this.
            Toast.makeText(context, "Please Enter a name", Toast.LENGTH_SHORT).show()
        }
    }
}

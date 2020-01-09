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


class SaveRunFragment : Fragment() {

    private lateinit var binding: FragmentSaveRunBinding
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
        run = args.run
        Log.d("TrackSaveRun", "Run: distance = ${run.distance}")
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

    private fun initMap(line: PolylineOptions) {
        mapView.getMapAsync { map ->
            googleMap = map
            val boundsBuilder = LatLngBounds.Builder()
            for(point: LatLng in line.points){
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
            googleMap.addPolyline(line)
        }
    }


    private fun saveRun() {
        val name: String = binding.nameEntry.text.toString()
        val distance: Float = run.distance
        val time: Float = run.timeElapsed.toFloat()

        val runValues = ContentValues()
        runValues.put(RunEntry.COLUMN_NAME, name)
        runValues.put(RunEntry.COLUMN_DISTANCE, distance)
        runValues.put(RunEntry.COLUMN_TIME, time)

        if (name.isNotBlank()) {
            context!!.contentResolver.insert(RunContract.CONTENT_URI, runValues)
            Toast.makeText(context, "Run: '$name' has been saved", Toast.LENGTH_LONG).show()
            fragmentManager!!.popBackStack()
        } else {
            Toast.makeText(context, "Please Enter a name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readFromDB() {
        val cursor = context!!.contentResolver.query(RunContract.CONTENT_URI, null, null, null, null)!!
        for (i in 0 until cursor.count) {
            cursor.moveToNext()
            val id = cursor.getInt(cursor.getColumnIndex(RunEntry.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(RunEntry.COLUMN_NAME))
            val distance = cursor.getFloat(cursor.getColumnIndex(RunEntry.COLUMN_DISTANCE))
            val time = cursor.getFloat(cursor.getColumnIndex(RunEntry.COLUMN_TIME))

            Log.d("TrackDB", "Reading: id=$id, name=$name, distance=$distance, time=$time")
        }
        cursor.close()
    }


}

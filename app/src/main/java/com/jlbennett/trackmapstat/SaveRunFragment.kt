package com.jlbennett.trackmapstat


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.jlbennett.trackmapstat.database.RunContract
import com.jlbennett.trackmapstat.databinding.FragmentSaveRunBinding
import com.jlbennett.trackmapstat.database.RunContract.RunEntry


class SaveRunFragment : Fragment() {

    private lateinit var binding: FragmentSaveRunBinding
    private val args: SaveRunFragmentArgs by navArgs()
    private lateinit var run: Run


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

        binding.saveButton.setOnClickListener { saveRun() }
        binding.readButton.setOnClickListener { readFromDB() }

        return binding.root
    }

    private fun saveRun() {
        val name: String = binding.nameEntry.text.toString()
        //TODO validate this name variable
        val distance: Float = run.distance
        val time: Float = run.timeElapsed.toFloat()

        val runValues = ContentValues()
        runValues.put(RunEntry.COLUMN_NAME, name)
        runValues.put(RunEntry.COLUMN_DISTANCE, distance)
        runValues.put(RunEntry.COLUMN_TIME, time)

        context!!.contentResolver.insert(RunContract.CONTENT_URI, runValues)
    }

    private fun readFromDB() {
        val cursor = context!!.contentResolver.query(RunContract.CONTENT_URI, null, null, null, null)!!
        for(i in 0 until cursor.count) {
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

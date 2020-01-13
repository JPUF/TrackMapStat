package com.jlbennett.trackmapstat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.jlbennett.trackmapstat.database.RunContract
import com.jlbennett.trackmapstat.database.RunContract.RunEntry
import com.jlbennett.trackmapstat.databinding.FragmentViewSimpleRunBinding


class ViewSimpleRunFragment : Fragment() {

    private lateinit var binding: FragmentViewSimpleRunBinding
    private val args: ViewSimpleRunFragmentArgs by navArgs()
    private var runID: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_simple_run, container, false)
        runID = args.runId


        updateFields()
        return binding.root
    }

    private fun updateFields() {
        val cursor = context!!.contentResolver.query(RunContract.CONTENT_URI, null, null, null, null)!!

        for (i in 0 until cursor.count) {
            cursor.moveToNext()
            val id = cursor.getInt(cursor.getColumnIndex(RunEntry.COLUMN_ID))
            if (id == runID) {
                val name = cursor.getString(cursor.getColumnIndex(RunEntry.COLUMN_NAME))
                val distance = cursor.getFloat(cursor.getColumnIndex(RunEntry.COLUMN_DISTANCE))
                val time = cursor.getLong(cursor.getColumnIndex(RunEntry.COLUMN_TIME))
                binding.nameText.text = "Name: $name"
                binding.distanceText.text = "Distance: ${"%.2f".format(distance)}m"
                val seconds = (time / 1000000000)
                val minutes = seconds / 60
                val hours = minutes / 60
                binding.timeText.text = "Time: ${"%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)}"
                cursor.close()
            }
        }
    }
}

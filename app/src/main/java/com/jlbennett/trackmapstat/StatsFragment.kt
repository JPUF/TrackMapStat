package com.jlbennett.trackmapstat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.jlbennett.trackmapstat.database.RunContract
import com.jlbennett.trackmapstat.databinding.FragmentStatsBinding


class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stats, container, false)

        calculateStats()

        return binding.root
    }

    private fun calculateStats() {
        val cursor = context!!.contentResolver.query(RunContract.CONTENT_URI, null, null, null, null)!!
        var totalDistance = 0F
        var totalTime = 0L

        for (i in 0 until cursor.count) {
            cursor.moveToNext()
            totalDistance += cursor.getFloat(cursor.getColumnIndex(RunContract.RunEntry.COLUMN_DISTANCE))
            totalTime += cursor.getLong(cursor.getColumnIndex(RunContract.RunEntry.COLUMN_TIME))
        }
        cursor.close()

        displayStats(totalDistance, totalTime)
    }

    private fun displayStats(distance: Float, time: Long) {
        val kilometers = distance / 1000F
        binding.totalDistanceText.text = "Total Distance Run: ${"%.2f".format(kilometers)}km"
        val seconds = (time / 1000000000)
        val minutes = seconds / 60
        val hours = minutes / 60
        binding.totalTimeText.text = "Total Time Running: ${"%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)}"
        val metersPerSecond = distance / seconds
        val kPerHour = metersPerSecond * 3.6
        binding.overallPaceText.text = "Overall Pace: ${"%.2f".format(kPerHour)}km/h"
    }
}

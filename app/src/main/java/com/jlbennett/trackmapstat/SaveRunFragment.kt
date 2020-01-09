package com.jlbennett.trackmapstat


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.jlbennett.trackmapstat.databinding.FragmentSaveRunBinding


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

        return binding.root
    }


}

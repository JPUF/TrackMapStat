package com.jlbennett.trackmapstat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jlbennett.trackmapstat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.trackCard.setOnClickListener {
            //GOTO TrackFragment
        }
    }
}

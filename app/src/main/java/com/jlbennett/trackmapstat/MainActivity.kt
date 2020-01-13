package com.jlbennett.trackmapstat

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.jlbennett.trackmapstat.databinding.ActivityMainBinding


/*
    This is the underlying Activity component.
    To display UI elements, each Fragment is inflated on top of this Activity.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Runtime registering of the Broadcast Receiver
        val receiver: BroadcastReceiver = LocationStateReceiver()
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(receiver, filter)
    }
}

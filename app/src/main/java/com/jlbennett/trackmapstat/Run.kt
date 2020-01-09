package com.jlbennett.trackmapstat

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Run(
    var distance: Float,
    var timeElapsed: Long,
    var timeStarted: Long?,
    var latestLocation: Location?,
    var routeLine: PolylineOptions,
    var runStarted: Boolean
) : Parcelable
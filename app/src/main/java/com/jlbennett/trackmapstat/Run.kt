package com.jlbennett.trackmapstat

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.parcel.Parcelize

/*
    The 'Model' data class to represent a user's run.
    It is Parcelable so that instances can be passed as a 'SafeArg' when navigating between Fragments.
 */
@Parcelize
data class Run(
    var name: String?,
    var distance: Float,
    var timeElapsed: Long,
    var timeStarted: Long?,
    var latestLocation: Location?,
    var routeLine: PolylineOptions,
    var runStarted: Boolean
) : Parcelable
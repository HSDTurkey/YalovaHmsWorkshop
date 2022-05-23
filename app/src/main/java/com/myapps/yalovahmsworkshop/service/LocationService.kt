package com.myapps.yalovahmsworkshop.service

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.huawei.hmf.tasks.Task
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun getLastLocation(lastKnownLocation: (LatLng) -> Unit) {
        val mFusedLocationProviderClient: FusedLocationProviderClient? = LocationServices.getFusedLocationProviderClient(context)

        var latLng: LatLng
        try {
            val lastLocation: Task<Location> = mFusedLocationProviderClient!!.lastLocation
            lastLocation.addOnSuccessListener {
                if (it == null) {
                    latLng = LatLng(0.0, 0.0)
                    lastKnownLocation(latLng)
                } else {
                    latLng = LatLng(it.latitude, it.longitude)
                    lastKnownLocation(latLng)
                }
            }.addOnFailureListener {
                Log.e("LocationService", "getLastLocation exception:" + it.message)
            }
        } catch (e: Exception) {
            Log.e("LocationService", "getLastLocation exception:" + e.message)
        }
    }
}
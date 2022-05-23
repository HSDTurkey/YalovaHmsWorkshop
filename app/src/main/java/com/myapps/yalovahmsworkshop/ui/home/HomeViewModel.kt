package com.myapps.yalovahmsworkshop.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.maps.model.LatLng
import com.myapps.yalovahmsworkshop.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val locationService: LocationService
) : ViewModel() {

    private val _lastKnownLocation = MutableLiveData<LatLng?>()
    val lastKnownLocation: LiveData<LatLng?>
        get() = _lastKnownLocation

    fun setLastKnownLocation(latLng: LatLng?) {
        _lastKnownLocation.value = latLng
    }

    fun checkPermissions() = locationService.checkPermission()
}
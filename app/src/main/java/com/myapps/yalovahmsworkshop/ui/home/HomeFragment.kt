package com.myapps.yalovahmsworkshop.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.myapps.yalovahmsworkshop.R
import com.myapps.yalovahmsworkshop.databinding.FragmentHomeBinding
import com.myapps.yalovahmsworkshop.utils.LocationUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding

    private lateinit var huaweiMap: HuaweiMap
    private lateinit var locationUtil: LocationUtil
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationUtil = LocationUtil(requireActivity())

        binding.huaweiMap.apply {
            onCreate(null)
            getMapAsync(this@HomeFragment)
        }
    }

    override fun onMapReady(map: HuaweiMap) {
        checkLocationPermission()
        huaweiMap = map
        huaweiMap.apply {
            isMyLocationEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isZoomControlsEnabled = true
        }
        observeAndAddTaxi()
    }

    private fun checkLocationPermission() {
        if (homeViewModel.checkPermissions()) {
            getLastKnownLocation()
        } else {
            getPermission()
        }
    }

    private fun getPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                2
            )
        } else {
            Log.e("TAG", "PERMISSION GRANTED")
        }
    }

    private fun observeAndAddTaxi() {
        homeViewModel.lastKnownLocation.observe(viewLifecycleOwner) {
            it?.let { latLang ->
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18f))
                huaweiMap.clear()
                addTaxi(latLang)
            }
        }
    }

    private fun addTaxi(latLng: LatLng) {
        if (marker != null) {
            marker!!.remove()
        }

        val startLat = latLng.latitude - 0.001
        val endLat = latLng.latitude + 0.001
        val startLng = latLng.longitude - 0.001
        val endLng = latLng.longitude + 0.001

        for (i in 0..5) {
            marker = huaweiMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        Random.nextDouble(startLat, endLat),
                        Random.nextDouble(startLng, endLng)
                    )
                )
                    .anchor(0.5f, 0.9f)
                    .title("HiTaxi")
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_taxi
                            )?.toBitmap()
                        )
                    )
            )
        }
    }

    private fun getLastKnownLocation() {
        homeViewModel.locationService.getLastLocation { lastLocation ->
            if (lastLocation.latitude != 0.0) {
                homeViewModel.setLastKnownLocation(lastLocation)
            } else {
                locationUtil.checkLocationSettings {
                    homeViewModel.setLastKnownLocation(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.huaweiMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.huaweiMap.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.huaweiMap.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.huaweiMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.huaweiMap.onLowMemory()
    }
}
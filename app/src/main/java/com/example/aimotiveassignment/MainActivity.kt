package com.example.aimotiveassignment

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aimotiveassignment.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {

        private const val REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Mapbox.getInstance(this)
        Mapbox.setApiKey(BuildConfig.MAPBOX_API_KEY)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.mapReady.observe(this) { ready ->

            if (ready) {
                startPermissionRequestFlow()
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(viewModel)
        }
    }

    private fun startPermissionRequestFlow() {

        if (hasLocationPermission()) {
            requestLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {

        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                locationRequestSuccessListener(location)
            }.addOnFailureListener { _ ->
                viewModel.navigateToDefaultLocation()
            }
    }

    private fun locationRequestSuccessListener(location: Location?) {

        location?.let {
            viewModel.navigateTo(Point.fromLngLat(it.longitude, it.latitude))
        } ?: run {
            viewModel.navigateToDefaultLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            } else {
                viewModel.navigateToDefaultLocation()
            }
        }
    }
}
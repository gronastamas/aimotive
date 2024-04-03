package com.example.aimotiveassignment

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style

class MainViewModel : ViewModel(), OnMapReadyCallback {

    private val defaultCoordinates: Point = Point.fromLngLat(19.035371, 47.527419)
    private val file =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/name.txt"

    private lateinit var mapBoxMap: MapboxMap

    private val _mapReady = MutableLiveData<Boolean>()
    val mapReady: LiveData<Boolean> = _mapReady

    companion object {

        private const val STYLE_URI =
            "https://api.maptiler.com/maps/streets/style.json?key=RQU1GHuSBcRaSDu70yxE"
        private const val NAVIGATION_ANIMATION_DURATION = 1500
        private const val TILT_VALUE = 20.0
        private const val ZOOM_VALUE = 18.0

        init {
            System.loadLibrary("aimotiveassignment")
        }
    }

    fun navigateToDefaultLocation() {

        navigateTo(defaultCoordinates)
    }

    fun navigateTo(coordinates: Point) {

        storeCoordinates(file, coordinates.latitude(), coordinates.longitude())

        val position: CameraPosition = CameraPosition.Builder()
            .target(LatLng(coordinates.latitude(), coordinates.longitude()))
            .tilt(TILT_VALUE)
            .zoom(ZOOM_VALUE)
            .build()

        mapBoxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(position),
            NAVIGATION_ANIMATION_DURATION
        )
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        this.mapBoxMap = mapboxMap
        this._mapReady.value = true

        mapboxMap.setStyle(Style.Builder().fromUri(STYLE_URI)) { _ -> }
    }

    private external fun storeCoordinates(filePath: String, latitude: Double, longitude: Double)
}
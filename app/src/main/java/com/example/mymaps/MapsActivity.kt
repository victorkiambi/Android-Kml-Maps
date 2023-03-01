package com.example.mymaps

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.kml.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val layer = KmlLayer(map, R.raw.samburu, this)
        layer.addLayerToMap()

        val pathPoints = ArrayList<LatLng>()
        if (layer.containers != null) {
            for (container in layer.containers) {
                if (container.hasPlacemarks()) {
                    for (placemark in container.placemarks) {
                        val geometry = placemark.geometry
                        when (geometry.geometryType) {
                            "MultiGeometry" -> {
                                val kmlMultiGeometry = geometry as KmlMultiGeometry
                                for (kmlGeometry in kmlMultiGeometry.geometryObject) {
                                    when (kmlGeometry.geometryType) {
                                        "Polygon" -> {
                                            val kmlPolygon = kmlGeometry as KmlPolygon
                                            for (latLng in kmlPolygon.outerBoundaryCoordinates) {
                                                pathPoints.add(latLng!!)
                                            }
                                        }
                                        "LineString" -> {
                                            val kmlLineString = kmlGeometry as KmlLineString
                                            for (latLng in kmlLineString.geometryObject) {
                                                pathPoints.add(latLng!!)
                                            }
                                        }
                                        "Point" -> {
                                            val kmlPoint = kmlGeometry as KmlPoint
                                            pathPoints.add(kmlPoint.geometryObject)
                                        }
                                        "MultiGeometry" -> {
                                            val kmlMultiGeometry1 = kmlGeometry as KmlMultiGeometry
                                            Log.e("MultiGeometry", "Working")
                                            for (kmlGeometry1 in kmlMultiGeometry1.geometryObject) {
                                                when (kmlGeometry1.geometryType) {
                                                    "Polygon" -> {
                                                        val kmlPolygon = kmlGeometry1 as KmlPolygon
                                                        Log.e("Polygon", "Working")
                                                        for (latLng in kmlPolygon.outerBoundaryCoordinates) {
                                                            pathPoints.add(latLng!!)
                                                        }
                                                    }
                                                    "LineString" -> {
                                                        val kmlLineString = kmlGeometry1 as KmlLineString
                                                        Log.e("LineString", "Working")
                                                        for (latLng in kmlLineString.geometryObject) {
                                                            pathPoints.add(latLng!!)
                                                        }
                                                    }
                                                    "Point" -> {
                                                        val kmlPoint = kmlGeometry1 as KmlPoint
                                                        Log.e("Point", "Working")
                                                        pathPoints.add(kmlPoint.geometryObject)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Create LatLngBounds of the outer coordinates of the polygon
            val builder = LatLngBounds.Builder()
            for (latLng in pathPoints) {
                builder.include(latLng)
                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, 1))
            }
        }
    }
}
package com.example.ops_psw

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.ops_psw.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var markerPSW: Marker
    private var logoPosition: GeoPoint? = null
    private val icons = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))


        markerPSW = Marker(binding.map)


        val mapController = binding.map.controller
        mapController.setZoom(20.5)
        val startPoint = GeoPoint(55.80722365980607, 37.533340468674496);
        mapController.setCenter(startPoint)


        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                when (binding.toggleButtonDirect.isChecked) {
                    true -> setUpIconDirections(p)
                    false -> setUpLogo(p)
                }
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val overlayEvents = MapEventsOverlay(mReceive)
        binding.map.overlays.add(overlayEvents)


        binding.buttonCenter.setOnClickListener {
            mapController.animateTo(logoPosition)
            mapController.zoomTo(8.3)
        }

        binding.toggleButtonDirect.setOnClickListener() {
            if (!binding.toggleButtonDirect.isChecked) removeDirections()
        }

    }


    private fun setUpLogo(p: GeoPoint?) {
        Log.d("Tapp", "$p")
        markerPSW.position = p
        logoPosition = p
        markerPSW.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        markerPSW.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.logo_psw)
        binding.map.overlays.add(markerPSW)
        binding.map.invalidate()
        binding.buttonCenter.isEnabled = true
    }

    private fun setUpIconDirections(p: GeoPoint?) {
        drawLines(p)
        val markerIcon = Marker(binding.map)
        markerIcon.position = p
        val drawable =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.dot)
        markerIcon.icon = drawable
        markerIcon.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        markerIcon.title = "Icon"
        binding.map.overlays.add(markerIcon)
        icons.add(markerIcon)
        binding.map.invalidate()
    }

    private fun drawLines(p: GeoPoint?) {
        val polyline = Polyline(binding.map)
        val points = ArrayList<GeoPoint?>()
        points.add(logoPosition)
        points.add(p)
        polyline.outlinePaint.color = Color.RED
        polyline.outlinePaint.strokeWidth = 5f
        polyline.setPoints(points)
        binding.map.overlays.add(polyline)
    }

    private fun removeDirections() {
        icons.filter { it.title == "Icon" }
        val polylines = binding.map.overlays.filter { it is Polyline }
        binding.map.overlays.removeAll(icons)
        binding.map.overlays.removeAll(polylines)
        binding.map.invalidate()
    }

}
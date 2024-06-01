package com.example.vehicalinformation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.json.JSONObject
import java.io.IOException
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MapViewer : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var client: OkHttpClient
    private lateinit var city: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_viewer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        map = findViewById(R.id.map)
        city=findViewById(R.id.city)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        client = OkHttpClient()

        val cityName = intent.getStringExtra("city").toString()
        city.text = cityName ;
            if (cityName.isNotEmpty()) {
                fetchCoordinates(cityName)
            }
    }
        private fun fetchCoordinates(cityName: String) {

            val request = Request.Builder()
                .url("https://address-from-to-latitude-longitude.p.rapidapi.com/geolocationapi?address=$cityName")
                .get()
                .addHeader("X-RapidAPI-Key", "41a2e8fa24mshc7f48d1ae692897p17c32djsn047e490e6301")
                .addHeader("X-RapidAPI-Host", "address-from-to-latitude-longitude.p.rapidapi.com")
                .build()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()

                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    response.body?.let {

                        val responseData = it.string()
                        val json = JSONObject(responseData)
                        val latitude = json.getJSONArray("Results").getJSONObject(0).getDouble("latitude")
                        val longitude = json.getJSONArray("Results").getJSONObject(0).getDouble("longitude")

                        // Update the map on the main thread
                        Handler(Looper.getMainLooper()).post {
                            updateMap(latitude, longitude)
                        }
                    }
                }
            })
        }
        private fun updateMap(latitude: Double, longitude: Double) {
            val startPoint = GeoPoint(latitude, longitude)
            val mapController = map.controller
            mapController.setZoom(13.0)
            mapController.setCenter(startPoint)

            // Add a marker to the map
            val startMarker = Marker(map)
            startMarker.position = startPoint
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.title = "Location: $latitude, $longitude"
            map.overlays.clear()
            map.overlays.add(startMarker)
        }
    override fun onResume() {
        super.onResume()
        map.onResume()
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
package ir.co.holoo.sinamap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.co.holoo.sinamap.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val map = binding.map

        //map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(16.0)

        // Initialize the OpenStreetMap configuration
        val sharedPreferences = getSharedPreferences("sina_app_preferences", Context.MODE_PRIVATE)
        Configuration.getInstance().load(applicationContext, sharedPreferences)

        // Initialize the MapView
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Set the map center to Tehran
        val tehran = GeoPoint(35.6892, 51.3890)
        mapController.setCenter(tehran)


        val startPoint = GeoPoint(35.6892, 51.3890)
        val endPoint = GeoPoint(35.6992, 51.4000)
        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(startPoint)
        waypoints.add(endPoint)

        GlobalScope.launch(Dispatchers.IO) {
            val roadManager = OSRMRoadManager(this@MainActivity, "test")
            val road = roadManager.getRoad(waypoints)
            withContext(Dispatchers.Main) {
                val roadOverlay = RoadManager.buildRoadOverlay(road)
                binding.map.overlays.add(roadOverlay)
            }
        }


        binding.btnGoMyLocation.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val latitude = location?.latitude
                val longitude = location?.longitude

                Toast.makeText(
                    this,
                    (latitude.toString() + ", " + longitude.toString()),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Permission is not granted. Request for permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    123456
                )
            }

        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123456) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val latitude = location?.latitude
                val longitude = location?.longitude

                Toast.makeText(
                    this,
                    (latitude.toString() + ", " + longitude.toString()),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}
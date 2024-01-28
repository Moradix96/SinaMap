package ir.co.holoo.sinamap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem


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

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.fabGoHome.setOnClickListener {
            Log.d("TAG", "btnGoMyLocation Clicked")

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("TAG", "PERMISSION_WAS_GRANTED")


                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val latitude = location?.latitude
                val longitude = location?.longitude
                Log.d("TAG", "Goto " + latitude + "," + longitude)
                Toast.makeText(this, "اینجا: " + latitude + "," + longitude, Toast.LENGTH_SHORT)
                    .show()
                if (latitude != null && longitude != null) {
                    val myPoint1 = GeoPoint(latitude, longitude)
                    addMarker(
                        map,
                        latitude,
                        longitude,
                        R.drawable.map_pin_svgrepo_com,
                        "موقعیت کنونی شما"
                    )
                    mapController.setCenter(myPoint1)
                    mapController.setZoom(19.0)
                    binding.map.controller.animateTo(myPoint1)
                } else {
                    Log.d("TAG", "Current location is null")
                    Toast.makeText(
                        this,
                        "مشکلی هنگام دریافت موقعیت کنونی شما رخ داد!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Log.e("TAG", "Permission Denied")

                // Permission is not granted. Request for permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
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

                val location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val latitude = location?.latitude
                val longitude = location?.longitude
                Log.d("TAG", "Goto " + latitude + "," + longitude)
                Toast.makeText(this, "اینجا: " + latitude + "," + longitude, Toast.LENGTH_SHORT)
                    .show()
                if (latitude != null && longitude != null) {
                    val myPoint1 = GeoPoint(latitude, longitude)
                    binding.map.controller.setCenter(myPoint1)
                    binding.map.controller.setZoom(19.0)
                    binding.map.controller.animateTo(myPoint1)
                } else {
                    Log.d("TAG", "Current location is null")
                    Toast.makeText(
                        this,
                        "مشکلی هنگام دریافت موقعیت کنونی شما رخ داد!",
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
    }

    fun addMarker(map: MapView, lat: Double, lon: Double, resourceId: Int, toastMessage: String) {
        // Set the map's center to your location
        val mapController = map.controller
        val currentLocation = GeoPoint(lat, lon) // replace lat, lon with your coordinates
        mapController.setCenter(currentLocation)

        // Create an OverlayItem to mark your location
        val myLocationOverlayItem = OverlayItem("اینجا", "موقعیت کنونی", currentLocation)

        // Set a custom marker if you want (optional)
        val markerDrawable = ContextCompat.getDrawable(applicationContext, resourceId)
        myLocationOverlayItem.setMarker(markerDrawable)

        // Create an ItemizedOverlayWithFocus and add your OverlayItem
        val items = ArrayList<OverlayItem>()
        items.add(myLocationOverlayItem)
        val markersOverlay = ItemizedOverlayWithFocus(items,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    // Your action here on marker tap
                    Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    // Your action here on marker long press
                    return false
                }
            }, applicationContext
        )

        // Add the overlay to the MapView
        map.overlays.add(markersOverlay)
    }


}
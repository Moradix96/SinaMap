package ir.co.holoo.sinamap.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.co.holoo.sinamap.R
import ir.co.holoo.sinamap.databinding.ActivityMainBinding
import ir.co.holoo.sinamap.dialogs.GoDialogFragment
import ir.co.holoo.sinamap.dialogs.GoListener
import ir.co.holoo.sinamap.model.Place
import ir.co.holoo.sinamap.utils.DBHelper
import ir.co.holoo.sinamap.utils.DBHelper2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var map: MapView
    private lateinit var mapController: IMapController

    private var defaultLocation = GeoPoint(35.736330956312294, 51.46915335841092)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatabase()

        map = binding.map
        mapController = map.controller

        //map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(true)
        mapController.setZoom(16.0)

        // Initialize the OpenStreetMap configuration
        val sharedPreferences = getSharedPreferences("sina_app_preferences", Context.MODE_PRIVATE)
        Configuration.getInstance().load(applicationContext, sharedPreferences)

        // Initialize the MapView
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Set the map center to Tehran
        showTehran()

        //drawExamplePath()


        calculateCurrentLocation()

        binding.btnSearch.setOnClickListener {
            val goDialog = GoDialogFragment()
            goDialog.goListener = object : GoListener {
                override fun go(lat: Double, lon: Double) {
                    drawRoute(defaultLocation, GeoPoint(lat, lon))
                    /*Toast.makeText(this@MainActivity, "GO" + lat + " " + lon, Toast.LENGTH_SHORT)
                        .show()*/
                }
            }
            goDialog.show(supportFragmentManager, "GoDialogFragment")
        }

        binding.fabGoHome.setOnClickListener {
            addMarker(
                map,
                defaultLocation.latitude,
                defaultLocation.longitude,
                R.drawable.map_pin_svgrepo_com,
                "موقعیت کنونی شما"
            )
            mapController.setCenter(defaultLocation)
            mapController.setZoom(19.0)
            mapController.animateTo(defaultLocation)
        }

        binding.btnShowList.setOnClickListener {
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
        }

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                showPopup(geoPoint)
                return true
            }

            override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                // Handle long press if needed
                return false
            }
        }

        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(overlayEvents)

        loadMapPoints()
    }

    private fun calculateCurrentLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "PERMISSION_WAS_GRANTED")

            getCurrentLocationCore()
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

    @SuppressLint("MissingPermission")
    private fun getCurrentLocationCore() {
        val providers = locationManager.getProviders(true)
        var mostAccurateLocation: Location? = null

        //Toast.makeText(this, "Count: " + providers.size, Toast.LENGTH_SHORT).show()
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // New location update
                if (mostAccurateLocation == null || location.accuracy < mostAccurateLocation!!.accuracy) {
                    mostAccurateLocation = location
                    val latitude = mostAccurateLocation?.latitude
                    val longitude = mostAccurateLocation?.longitude

                    Log.d("TAG", "Here: $latitude,$longitude")

                    if (latitude != null && longitude != null) {
                        defaultLocation.latitude = latitude
                        defaultLocation.longitude = longitude
                        Toast.makeText(
                            this@MainActivity,
                            "موقعیت مکانی شما به روز شد.",
                            Toast.LENGTH_LONG
                        ).show()
                        locationManager.removeUpdates(this)
                    } else {
                        Log.d("TAG", "Current location is null")
                        Toast.makeText(
                            this@MainActivity,
                            "مشکلی هنگام دریافت موقعیت کنونی شما رخ داد!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // Request location updates from all providers
        for (provider in providers) {
            locationManager.requestLocationUpdates(provider, 0L, 0f, locationListener)
        }
    }

    private fun drawExamplePath() {
        drawRoute(GeoPoint(35.6892, 51.3890), GeoPoint(35.6992, 51.4000))
    }

    private fun drawRoute(sourceGeoPoint: GeoPoint, targetGeoPoint: GeoPoint) {
        clearRoadOverlays(map)

        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(sourceGeoPoint)
        waypoints.add(targetGeoPoint)

        GlobalScope.launch(Dispatchers.IO) {
            val roadManager = OSRMRoadManager(this@MainActivity, "test")
            val road = roadManager.getRoad(waypoints)
            withContext(Dispatchers.Main) {
                val roadOverlay = RoadManager.buildRoadOverlay(road)
                map.overlays.add(roadOverlay)
            }
        }
    }

    private fun showTehran() {
        val tehran = GeoPoint(35.6892, 51.3890)
        mapController.setCenter(tehran)
    }

    private fun loadMapPoints() {
        val list: ArrayList<Place> = DBHelper2.getPlaces(this)
        for (item in list) {
            addMarker(
                map,
                item.lat,
                item.lon,
                R.drawable.map_pin_svgrepo_com,
                item.name
            )
        }
    }

    private fun setupDatabase() {
        val databaseHelper = DBHelper(this)

        try {
            databaseHelper.createDatabase() // Create the database from the assets folder.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showPopup(geoPoint: GeoPoint) {
        // Convert the GeoPoint to a Pixel
        val point = map.projection.toPixels(geoPoint, null)

        // Inflate the popup_layout.xml
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = layoutInflater.inflate(R.layout.main_popup_menu, null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            customView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Set an elevation for the popup window
        popupWindow.elevation = 10.0F

        // Set the popup window to be focusable
        popupWindow.isFocusable = true

        // Set the popup window to be outside touchable
        popupWindow.isOutsideTouchable = true

        // Get the widgets reference from custom view
        val buttonRoute = customView.findViewById<TextView>(R.id.menu_route)
        val buttonAdd = customView.findViewById<TextView>(R.id.menu_add)

        // Set click listeners for popup menu buttons
        buttonRoute.setOnClickListener {
            drawRoute(defaultLocation, geoPoint)
            popupWindow.dismiss()
        }
        buttonAdd.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
            intent.putExtra("lat", geoPoint.latitude)
            intent.putExtra("lon", geoPoint.longitude)
            launcher.launch(intent)
            popupWindow.dismiss()
        }

        // Finally, show the popup window on the map
        popupWindow.showAtLocation(map, Gravity.NO_GRAVITY, point.x, point.y)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                addMarker(
                    map,
                    data!!.getDoubleExtra("lat", 0.0),
                    data.getDoubleExtra("lon", 0.0),
                    R.drawable.map_pin_svgrepo_com,
                    data.getStringExtra("name")!!
                )
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
                getCurrentLocationCore()
            }
        }
    }

    private val items = ArrayList<OverlayItem>()
    private lateinit var markersOverlay: ItemizedOverlayWithFocus<OverlayItem>

    fun addMarker(map: MapView, lat: Double, lon: Double, resourceId: Int, toastMessage: String) {
        val location = GeoPoint(lat, lon)

        // Create an OverlayItem to mark your location
        val myLocationOverlayItem = OverlayItem(toastMessage, toastMessage, location)

        // Set a custom marker if you want (optional)
        val markerDrawable = ContextCompat.getDrawable(applicationContext, resourceId)
        myLocationOverlayItem.setMarker(markerDrawable)

        // Add your OverlayItem to the existing items list
        items.add(myLocationOverlayItem)

        // If markersOverlay is already initialized, remove it from the map overlays
        if (::markersOverlay.isInitialized) {
            map.overlays.remove(markersOverlay)
        }

        // Create a new ItemizedOverlayWithFocus with the updated items list
        markersOverlay = ItemizedOverlayWithFocus(
            items,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    // Your action here on marker tap
                    Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
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

    fun clearRoadOverlays(map: MapView) {
        map.overlays.removeAll { it is Polyline }
        map.invalidate()
    }

}

/*
class MyGoDialogFragment : GoDialogFragment() {
    override fun go(lat: Double, lon: Double) {
        //Toast.makeText(activity, "روی go کلیک شد", Toast.LENGTH_SHORT).show()

        //drawRoute(defaultLocation, geoPoint)

    }
}

*/


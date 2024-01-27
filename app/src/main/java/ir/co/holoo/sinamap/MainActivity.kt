package ir.co.holoo.sinamap

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.co.holoo.sinamap.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val map = binding.map

        //map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(15.0)

        // Initialize the OpenStreetMap configuration
        val sharedPreferences = getSharedPreferences("sina_app_preferences", Context.MODE_PRIVATE)
        Configuration.getInstance().load(applicationContext, sharedPreferences)

        // Initialize the MapView
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Set the map center to Tehran
        val tehran = GeoPoint(35.6892, 51.3890)
        mapController.setCenter(tehran)
    }

}
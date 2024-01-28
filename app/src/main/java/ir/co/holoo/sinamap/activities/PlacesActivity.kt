package ir.co.holoo.sinamap.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.co.holoo.sinamap.adapter.PlacesAdapter
import ir.co.holoo.sinamap.databinding.ActivityPlacesBinding
import ir.co.holoo.sinamap.model.Place
import ir.co.holoo.sinamap.utils.DBHelper2


class PlacesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlacesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val list: ArrayList<Place> = DBHelper2.getPlaces(this)
        Log.d("TAG", "onCreate: " + list.size)
        val adapter: PlacesAdapter = object : PlacesAdapter(list) {
            override fun onClick(place: Place) {
                Toast.makeText(
                    this@PlacesActivity,
                    "موقعیت: " + place.lat + ", " + place.lon,
                    Toast.LENGTH_SHORT
                ).show()
                /*val intent: Intent = Intent(
                    this@PlacesActivity,
                    DetailsActivity::class.java
                )*/
                //intent.putExtra("cn", name);
                //intent.putExtra("cid", rowid);
                //intent.putExtra("id", rowid)
                //startActivity(intent)
            }
        }
        binding.rv.setHasFixedSize(true)
        binding.rv.adapter = adapter
        adapter.notifyDataSetChanged()

    }


}
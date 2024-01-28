package ir.co.holoo.sinamap

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ir.co.holoo.sinamap.adapter.PlacesAdapter
import ir.co.holoo.sinamap.databinding.ActivityPlacesBinding
import ir.co.holoo.sinamap.model.Place


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
            override fun onClick(rowid: String?, name: String?) {
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
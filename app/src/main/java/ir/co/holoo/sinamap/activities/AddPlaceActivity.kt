package ir.co.holoo.sinamap.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.co.holoo.sinamap.databinding.ActivityAddPlaceBinding
import ir.co.holoo.sinamap.model.Place
import ir.co.holoo.sinamap.utils.DBHelper2
import org.osmdroid.util.GeoPoint


class AddPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlaceBinding
    private lateinit var geoPoint: GeoPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        geoPoint = GeoPoint(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lon", 0.0))
        binding.buttonSave.setOnClickListener {
            if (binding.editTextName.text.isBlank()) {
                Toast.makeText(this, "کادر نام نمی‌تواند خالی باشد", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DBHelper2.insertPlace(
                this,
                Place(
                    0,
                    binding.editTextName.text.toString(),
                    geoPoint.latitude,
                    geoPoint.longitude
                )
            )

            val resultIntent = Intent()
            resultIntent.putExtra("name", binding.editTextName.text.toString())
            resultIntent.putExtra("lat", geoPoint.latitude)
            resultIntent.putExtra("lon", geoPoint.longitude)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

    }


}
package ir.co.holoo.sinamap.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.co.holoo.sinamap.databinding.ActivityAddPlaceBinding


class AddPlaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

    }


}
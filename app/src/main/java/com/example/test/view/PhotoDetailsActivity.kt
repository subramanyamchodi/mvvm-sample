package com.example.test.view

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityPhotoDetailsBinding
import com.example.test.network.Photo

class PhotoDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Photo details"
        showImageDetails()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showImageDetails() {
        val photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable("photo", Photo::class.java)
        } else {
            intent.extras?.getParcelable("photo")
        }
        BindingUtils.loadImage(photo, binding.ivImage)
        binding.ivDescription.text = photo?.title
    }
}
package com.example.test.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.test.databinding.ActivityMainBinding
import com.example.test.network.Photo
import com.example.test.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhotoListActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var photoViewModel: PhotoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        photoViewModel = ViewModelProvider(this)[PhotoViewModel::class.java]
        viewBinding.recyclerView.adapter = photoViewModel.photoAdapter
        photoViewModel.getPhotosData()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    photoViewModel.photos.collectLatest {
                        if (it != null) {
                            photoViewModel.photoAdapter.submitList(it)
                        }
                    }
                }
                launch {
                    photoViewModel.loading.collectLatest {
                        viewBinding.progressCircular.visibility =
                            if (it) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    photoViewModel.error.collectLatest {
                        if (it.isNotEmpty()) {
                            Toast.makeText(this@PhotoListActivity, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                launch {
                    photoViewModel.selectedPhoto.collectLatest {
                        goToDetailsScreen(it)
                    }
                }
            }
        }
    }

    private fun goToDetailsScreen(photo: Photo) {
        startActivity(
            Intent(this, PhotoDetailsActivity::class.java).apply {
                putExtra("photo", photo)
            }
        )
    }
}
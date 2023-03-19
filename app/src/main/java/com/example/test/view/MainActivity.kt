package com.example.test.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.test.databinding.ActivityMainBinding
import com.example.test.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
                            initAdapter()
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
                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        if (viewBinding.recyclerView.adapter == null) {
            photoViewModel.photoAdapter.setOnItemClickListener {

            }
        }
    }
}
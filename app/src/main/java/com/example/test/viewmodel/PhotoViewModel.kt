package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.network.ApiResponse
import com.example.test.network.Photo
import com.example.test.network.PhotoRepository
import com.example.test.view.PhotoAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    val photoAdapter by lazy {
        PhotoAdapter()
    }

    private val _photos = MutableStateFlow<List<Photo>?>(null)
    val photos = _photos.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    fun getPhotos() {
        viewModelScope.launch {
            _loading.emit(true)
            val response = photoRepository.getPhotos()
            if (response.isSuccessful) {
                _photos.emit(response.body())
            }
            _loading.emit(false)
        }
    }

    fun getPhotosData() {
        viewModelScope.launch {
            _loading.emit(true)
            val response = photoRepository.getPhotosData()
            if (response is ApiResponse.Success) {
                _photos.emit(response.responseData)
            }
            if (response is ApiResponse.Error) {
                _error.emit(response.msg)
            }
            _loading.emit(false)
        }
    }


}
package com.example.test

import com.example.app.network.ApiResponse
import com.example.test.network.ApiService
import com.example.test.network.Photo
import com.example.test.network.PhotoRepository
import com.example.test.network.ResponseHandler
import com.example.test.viewmodel.PhotoViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(DelicateCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class PhotoViewModelTest {

    private lateinit var photoRepository: PhotoRepository
    private lateinit var viewModel: PhotoViewModel
    // Mock dependencies
    private val mockApiService = mock(ApiService::class.java)
    private val mockResponseHandler = mock(ResponseHandler::class.java)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Create PhotoRepository instance with mock dependencies
        Dispatchers.setMain(mainThreadSurrogate)
        photoRepository = PhotoRepository(mockApiService, mockResponseHandler)
        viewModel = PhotoViewModel(photoRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetPhotos() = runTest {
        // Mock ApiService response
        val photos = listOf(
            Photo(1, 1, "title 1", "https://via.placeholder.com/600/92c952", "https://via.placeholder.com/600/92c952"),
            Photo(2, 1, "title 2", "https://via.placeholder.com/600/771796", "https://via.placeholder.com/600/92c952")
        )
        val mockResponse = Response.success(photos)
        `when`(mockApiService.getPhotos()).thenReturn(mockResponse)
        backgroundScope.launch {
            viewModel.photos.collect {}
        }
        viewModel.getPhotos()
        advanceTimeBy(1000)
        println(viewModel.photos.value)
        println(photos)
        assertEquals(photos, viewModel.photos.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetPhotosData() = runTest {
        // Mock ApiService response
        val photos = listOf(
            Photo(1, 1, "title 1", "https://via.placeholder.com/600/92c952", "https://via.placeholder.com/600/92c952"),
            Photo(2, 1, "title 2", "https://via.placeholder.com/600/771796", "https://via.placeholder.com/600/92c952")
        )
        val mockResponse = ApiResponse.Success(
            photos, -1
        )
        `when`(photoRepository.getPhotosData()).thenReturn(mockResponse)
        val photoList = mutableListOf<Photo>()
        backgroundScope.launch {
            viewModel.photos.collectLatest {
                it?.forEach {
                    photoList.add(it)
                }
            }
        }
        photoRepository.getPhotosData()
        viewModel.getPhotosData()
        advanceTimeBy(1000)
        println("Actual data ->" + viewModel.photos.value)
        println("Expected data -->" +mockResponse.responseData)
        assertEquals(mockResponse.responseData, viewModel.photos.value)
    }

}
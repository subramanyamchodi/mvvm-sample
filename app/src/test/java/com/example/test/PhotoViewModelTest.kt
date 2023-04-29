package com.example.test

import app.cash.turbine.test
import com.example.app.network.ApiResponse
import com.example.test.network.ApiService
import com.example.test.network.Photo
import com.example.test.network.PhotoRepository
import com.example.test.network.ResponseHandler
import com.example.test.view.PhotoAdapter
import com.example.test.view.PhotoListActivity
import com.example.test.viewmodel.PhotoViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer
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
    private val mockPhotoAdapter = mock(PhotoAdapter::class.java)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Create PhotoRepository instance with mock dependencies
        Dispatchers.setMain(mainThreadSurrogate)
        photoRepository = PhotoRepository(mockApiService, mockResponseHandler)
        viewModel = PhotoViewModel(photoRepository, mockPhotoAdapter)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun testGetPhotosData() = runTest {
//        // Mock ApiService response
//        val photos = listOf(
//            Photo(
//                1,
//                1,
//                "title 1",
//                "https://via.placeholder.com/600/92c952",
//                "https://via.placeholder.com/600/92c952"
//            ),
//            Photo(
//                2,
//                1,
//                "title 2",
//                "https://via.placeholder.com/600/771796",
//                "https://via.placeholder.com/600/92c952"
//            )
//        )
//        val mockResponse = ApiResponse.Success(
//            photos, -1
//        )
//        `when`(photoRepository.getPhotosData()).thenReturn(mockResponse)
//        val photoList = mutableListOf<Photo>()
//        backgroundScope.launch {
//            viewModel.photos.collectLatest {
//                it?.forEach {
//                    photoList.add(it)
//                }
//            }
//        }
//        viewModel.getPhotosData()
//        advanceTimeBy(3000)
//        println(photoList)
//        assertEquals(mockResponse.responseData, photoList)
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testError() = runTest {
        val mockResponse = ApiResponse.Error<List<Photo>>(
            "No data found", -1, Any()
        )
        `when`(photoRepository.getPhotosData()).thenReturn(mockResponse)
        backgroundScope.launch {
            viewModel.error.collectLatest {}
        }
        viewModel.getPhotosData()
        advanceTimeBy(1000)
        assertEquals(mockResponse.msg, viewModel.error.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetPhotosDataTurbine() = runTest {
        // Mock ApiService response
        val photos = listOf(
            Photo(
                1,
                1,
                "title 1",
                "https://via.placeholder.com/600/92c952",
                "https://via.placeholder.com/600/92c952"
            ),
            Photo(
                2,
                1,
                "title 2",
                "https://via.placeholder.com/600/771796",
                "https://via.placeholder.com/600/92c952"
            )
        )
        val mockResponse = ApiResponse.Success(
            photos, -1
        )
        `when`(photoRepository.getPhotosData()).thenReturn(mockResponse)
        viewModel.photos.test {
            skipItems(1)
            viewModel.getPhotosData()
            val data = awaitItem()
            println(data)
            assertEquals(mockResponse.responseData, data)
        }
        viewModel.loading.test {
            skipItems(1)
            viewModel.getPhotosData()
            val second = awaitItem()
            println(second)
            assertEquals(true, second)
            val third = awaitItem()
            println(third)
            assertEquals(false, third)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
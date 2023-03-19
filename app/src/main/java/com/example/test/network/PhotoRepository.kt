package com.example.test.network

import com.example.app.network.ApiResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val apiService: ApiService,
    val responseHandler: ResponseHandler
) {

    suspend fun getPhotos() = apiService.getPhotos()

    suspend fun getPhotosData() = safeApiCall({
        apiService.getPhotosData()
    })

    suspend inline fun <T> safeApiCall(
        crossinline body: suspend () -> T,
        requestCode: Int = -1
    ): ApiResponse<T> {
        return try {
            responseHandler.handleSuccess(
                body.invoke(),
                requestCode
            )
        } catch (ex: Exception) {
            responseHandler.handleException(ex, requestCode)
        }
    }
}


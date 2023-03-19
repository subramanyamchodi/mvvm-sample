package com.example.test.network

import com.example.app.network.ApiResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


open class ResponseHandler @Inject constructor() {

    fun <T> handleSuccess(data: T, code: Int): ApiResponse<T> {
        return ApiResponse.Success(data, code)
    }

    fun <T> handleException(e: Exception, code: Int = 0): ApiResponse<T> {
        return when (e) {
            is HttpException -> {
                ApiResponse.Error(
                    "Http exception",
                    code,
                    errorBody = getErrorBody(e)
                )
            }

            is ConnectException -> ApiResponse.Error(
                "Failed to connect",
                code
            )

            is UnknownHostException -> {
                ApiResponse.Error(
                    "Unknown host",
                    code
                )
            }

            is SocketTimeoutException -> ApiResponse.Error(
                "time out",
                code
            )

            else -> ApiResponse.Error(
                "Error",
                code
            )
        }
    }

    private fun getErrorBody(e: HttpException): String {
        return try {
            Gson().fromJson(
                e.response()?.errorBody()?.byteStream()?.let {
                    String(it.readBytes())
                },
                String::class.java
            )
        } catch (ex: Exception) {
            "Error"
        }
    }

}

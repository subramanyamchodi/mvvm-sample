package com.example.app.network



sealed class ApiResponse <out T> {
    data class Success<T>(val responseData: T? = null, val requestCode: Int = -1): ApiResponse<T>()

    data class Error<T> (val msg: String, val requestCode: Int?, val errorBody: Any? = null): ApiResponse<T>()

    data class Loading<T>(val data: Int? = null): ApiResponse<T>()

    data class Loaded<T>(val data: T? = null): ApiResponse<T>()
}
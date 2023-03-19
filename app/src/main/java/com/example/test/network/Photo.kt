package com.example.test.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String
): Parcelable

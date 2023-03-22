package com.example.test.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.test.R
import com.example.test.network.Photo
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object BindingUtils {

    @JvmStatic
    @BindingAdapter("app:imageUrl")
    fun loadImage(view: ImageView, photo: Photo) {
        loadImage(photo, view)
    }

    fun loadImage(photo: Photo?, view: ImageView) {
        if (photo?.url.isNullOrEmpty().not()
            || photo?.thumbnailUrl.isNullOrEmpty().not()
        ) {
            Picasso.get()
                .load(photo?.thumbnailUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(view, object : Callback {
                    override fun onSuccess() {
                        Picasso.get()
                            .load(photo?.url) // image url goes here
                            .placeholder(view.drawable)
                            .into(view)
                    }

                    override fun onError(e: Exception?) {
                    }

                })
        }

    }
}
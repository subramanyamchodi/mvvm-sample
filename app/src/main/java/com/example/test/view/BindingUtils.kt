package com.example.test.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.test.R
import com.example.test.network.Photo
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

object BindingUtils {

    @JvmStatic
    @BindingAdapter("app:imageUrl")
    fun loadImage(view: ImageView, photo: Photo) {
        if (!photo.url.isEmpty()) {
            Picasso.get()
                .load(photo.thumbnailUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(view, object : Callback {
                    override fun onSuccess() {
                        Picasso.get()
                            .load(photo.url) // image url goes here
                            .placeholder(view.getDrawable())
                            .into(view);
                    }

                    override fun onError(e: Exception?) {
                    }

                })
        }
    }
}
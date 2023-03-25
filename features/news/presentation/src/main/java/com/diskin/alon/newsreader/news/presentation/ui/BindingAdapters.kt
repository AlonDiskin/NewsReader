package com.diskin.alon.newsreader.news.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let {
        Glide
            .with(imageView.context)
            .load(url)
            .centerCrop()
            .into(imageView)
    }
}
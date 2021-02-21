package com.davidchen.drawimg

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import lv.chi.photopicker.loader.ImageLoader

class GlideImageLoader : ImageLoader {

    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Glide.with(context)
            .load(uri)
            .asBitmap()
            .centerCrop()
            .into(view)
    }

}

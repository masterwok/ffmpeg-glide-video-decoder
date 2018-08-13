package com.masterwok.demoffmpegglidevideodecoder.glide

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.masterwok.ffmpegglidevideodecoder.FFmpegVideoDecoder


@GlideModule()
class GlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        registry.prepend(
                Registry.BUCKET_BITMAP
                , Uri::class.java
                , Bitmap::class.java
                , FFmpegVideoDecoder(glide.bitmapPool)
        )

        super.registerComponents(context, glide, registry)
    }


}

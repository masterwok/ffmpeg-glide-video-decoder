package com.masterwok.demoffmpegglidevideodecoder.glide

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.VideoDecoder
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.masterwok.ffmpegglidevideodecoder.FFmpegUriVideoDecoder


@GlideModule()
class GlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions().apply {
            set(VideoDecoder.TARGET_FRAME, VideoDecoder.DEFAULT_FRAME)
            set(VideoDecoder.FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }

        builder.setDefaultRequestOptions(requestOptions)

        super.applyOptions(context, builder)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        registry.prepend(
                Registry.BUCKET_BITMAP
                , Uri::class.java
                , Bitmap::class.java
                , FFmpegUriVideoDecoder(context, glide.bitmapPool, 0.03F)
        )

        super.registerComponents(context, glide, registry)
    }


}

package com.masterwok.demoffmpegglidevideodecoder.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.masterwok.demoffmpegglidevideodecoder.R
import com.masterwok.demoffmpegglidevideodecoder.glide.GlideApp

class MainActivity : AppCompatActivity() {

    private lateinit var imageViewDemo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViewComponents()

        demoVideoDecoder()
    }

    private fun demoVideoDecoder() = GlideApp
            .with(this)
            // TODO: This needs to be a video.
            .load(Uri.parse("https://github.com/masterwok/ffmpeg-glide-resource-decoder/blob/master/app/images/glide_logo.png"))
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageViewDemo)


    private fun bindViewComponents() {
        imageViewDemo = findViewById(R.id.image_view_demo)
    }
}

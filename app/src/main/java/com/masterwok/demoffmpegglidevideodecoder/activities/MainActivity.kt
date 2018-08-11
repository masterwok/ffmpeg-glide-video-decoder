package com.masterwok.demoffmpegglidevideodecoder.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
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

    private fun demoVideoDecoder() {

        GlideApp.with(this)
                .load(Uri.parse("https://github.com/bumptech/glide/raw/master/static/glide_logo.png"))
//                .centerCrop()
//                .signature(ObjectKey("${torrent.isBitmapReady}:${torrent.isFinished}"))
                .into(imageViewDemo)
    }


    private fun bindViewComponents() {
        imageViewDemo = findViewById(R.id.image_view_demo)
    }
}

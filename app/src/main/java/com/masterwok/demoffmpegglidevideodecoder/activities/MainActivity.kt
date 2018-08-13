package com.masterwok.demoffmpegglidevideodecoder.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.masterwok.demoffmpegglidevideodecoder.R
import com.masterwok.demoffmpegglidevideodecoder.glide.GlideApp

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutRoot: LinearLayout

    private val videoLocations = arrayOf(
            "http://techslides.com/demos/sample-videos/small.mp4"
            , "http://techslides.com/demos/sample-videos/small.webm"
            , "http://techslides.com/demos/sample-videos/small.ogv"
            , "http://techslides.com/demos/sample-videos/small.3gp"
            , "http://techslides.com/demos/sample-videos/small.flv"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViewComponents()

        videoLocations.forEach {
            val imageView = createImageView()

            loadImage(imageView, it)

            linearLayoutRoot.addView(imageView)
        }
    }

    private fun bindViewComponents() {
        linearLayoutRoot = findViewById(R.id.linear_layout_root)
    }

    private fun createImageView(): ImageView = ImageView(this).apply {
        layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT
                , 1F
        )
    }

    private fun loadImage(
            imageView: ImageView
            , source: String
    ) = GlideApp
            .with(this)
            .load(Uri.parse(source))
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)

}

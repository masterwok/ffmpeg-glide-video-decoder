package com.masterwok.demoffmpegglidevideodecoder.activities

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.masterwok.demoffmpegglidevideodecoder.R
import com.masterwok.demoffmpegglidevideodecoder.glide.GlideApp
import com.masterwok.ffmpegglidevideodecoder.FFmpegVideoDecoder

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutRoot: LinearLayout

    // Place videos you'd like to test here..
    private val videoLocations = arrayOf(
            "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"
            , "http://mirrors.standaloneinstaller.com/video-sample/page18-movie-4.avi"
            , "http://mirrors.standaloneinstaller.com/video-sample/Panasonic_HDC_TM_700_P_50i.flv"
            , "http://mirrors.standaloneinstaller.com/video-sample/DLP_PART_2_768k.m4v"
            , "http://mirrors.standaloneinstaller.com/video-sample/star_trails.mkv"
            , "http://mirrors.standaloneinstaller.com/video-sample/small.mts"
            , "http://mirrors.standaloneinstaller.com/video-sample/metaxas-keller-Bell.vob"
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
            .applyDefaultRequestOptions(RequestOptions().apply {
                set(FFmpegVideoDecoder.PERCENTAGE_DURATION, 0.03F)
                // set(FFmpegVideoDecoder.FRAME_AT_TIME, 1000 * 1000 * 1) // One second
            })
            .load(Uri.parse(source))
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)

}

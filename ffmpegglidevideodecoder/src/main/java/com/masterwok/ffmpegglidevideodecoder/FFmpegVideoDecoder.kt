package com.masterwok.ffmpegglidevideodecoder

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import wseemann.media.FFmpegMediaMetadataRetriever


/**
 * A Glide video decoder that uses FFmpegMediaMetadataRetriever to get bitmaps.
 * Consider using this decoder when Android's MediaMetadataRetriever fails to retrieve
 * a Bitmap. Set [@see PERCENTAGE_DURATION] to get a frame for some time a a given
 * percentage of the media duration. Set [@see FRAME_AT_TIME] (micro-seconds) to get a
 * frame at some time (micro-seconds).
 */
class FFmpegVideoDecoder constructor(
        private val bitmapPool: BitmapPool
) : ResourceDecoder<Uri, Bitmap> {
    companion object {
        const val Tag = "FFmpegVideoDecoder"

        /**
         * Get a frame for some time at a given percentage of the duration.
         */
        val PERCENTAGE_DURATION: Option<Float> = Option.memory(
                "com.masterwok.ffmpegglidevideodecoder.PercentageDuration"
                , 0.03F
        )

        /**
         * Get a frame at some time (micro-seconds).
         */
        val FRAME_AT_TIME: Option<Long> = Option.memory(
                "com.masterwok.ffmpegglidevideodecoder.FrameAtTime"
                , -1
        )
    }

    private fun FFmpegMediaMetadataRetriever.decodeOriginalFrame(
            frameTimeMicros: Long
            , frameOption: Int
    ): Bitmap? = getFrameAtTime(
            frameTimeMicros
            , frameOption
    )

    private fun FFmpegMediaMetadataRetriever.duration(): Long =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION).toLong()

    private fun FFmpegMediaMetadataRetriever.originalWidth(): Int =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()

    private fun FFmpegMediaMetadataRetriever.originalHeight(): Int =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()

    private fun FFmpegMediaMetadataRetriever.orientation(): Int =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).toInt()

    private fun FFmpegMediaMetadataRetriever.percentagePosition(percentagePosition: Float): Long =
            (duration() * 1000 * percentagePosition).toLong()

    override fun handles(source: Uri, options: Options): Boolean = true

    override fun decode(source: Uri, outWidth: Int, outHeight: Int, options: Options): Resource<Bitmap>? {
        val retriever = FFmpegMediaMetadataRetriever()
        val percentagePosition = options.get(PERCENTAGE_DURATION)!!
        val frameAtTime = options.get(FRAME_AT_TIME)!!
        val downSampleStrategy = options.get(DownsampleStrategy.OPTION)
                ?: DownsampleStrategy.NONE

        val bitmap: Bitmap?

        try {
            retriever.setDataSource(source.toString())

            bitmap = decodeFrame(
                    retriever
                    , if (frameAtTime >= 0) frameAtTime else retriever.percentagePosition(percentagePosition)
                    , FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    , outWidth
                    , outHeight
                    , downSampleStrategy
            )

        } finally {
            retriever.release()
        }

        return BitmapResource.obtain(bitmap, bitmapPool)
    }

    private fun decodeFrame(
            retriever: FFmpegMediaMetadataRetriever
            , timeUs: Long
            , frameOption: Int
            , outWidth: Int
            , outHeight: Int
            , strategy: DownsampleStrategy
    ): Bitmap? {
        var result: Bitmap? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
                && outWidth != com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                && outHeight != com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                && strategy != DownsampleStrategy.NONE
        ) {
            result = decodeScaledFrame(
                    retriever
                    , timeUs
                    , frameOption
                    , outWidth
                    , outHeight
                    , strategy
            )
        }

        return result ?: retriever.decodeOriginalFrame(timeUs, frameOption)
    }

    private fun decodeScaledFrame(
            retriever: FFmpegMediaMetadataRetriever
            , timeUs: Long
            , frameOption: Int
            , outWidth: Int
            , outHeight: Int
            , strategy: DownsampleStrategy
    ): Bitmap? {
        try {
            var originalWidth = retriever.originalWidth()
            var originalHeight = retriever.originalHeight()
            val orientation = retriever.orientation()

            if (orientation == 90 || orientation == 270) {
                val tmp = originalWidth
                originalWidth = originalHeight
                originalHeight = tmp
            }

            val scaleFactor = strategy.getScaleFactor(
                    originalWidth
                    , originalHeight
                    , outWidth
                    , outHeight
            )

            val decodeWidth = Math.round(scaleFactor * originalWidth)
            val decodeHeight = Math.round(scaleFactor * originalHeight)

            return retriever.getScaledFrameAtTime(
                    timeUs
                    , frameOption
                    , decodeWidth
                    , decodeHeight
            )

        } catch (ex: Exception) {
            if (Log.isLoggable(Tag, Log.DEBUG)) {
                Log.d(Tag, "Exception trying to decode frame on oreo+", ex)
            }
        }

        return null
    }

}
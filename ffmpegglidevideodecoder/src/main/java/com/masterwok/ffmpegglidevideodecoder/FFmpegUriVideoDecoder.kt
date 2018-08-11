package com.masterwok.ffmpegglidevideodecoder

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import wseemann.media.FFmpegMediaMetadataRetriever

class FFmpegUriVideoDecoder constructor(
        private val context: Context
        , private val bitmapPool: BitmapPool
        , private val frameAtDurationPercentage: Float
) : ResourceDecoder<Uri, Bitmap> {
    companion object {
        const val Tag = "FFmpegUriVideoDecoder"
    }

    private fun FFmpegMediaMetadataRetriever.decodeOriginalFrame(
            frameTimeMicros: Long
            , frameOption: Int
    ): Bitmap? = getFrameAtTime(
            frameTimeMicros
            , frameOption
    )

    private fun FFmpegMediaMetadataRetriever.originalWidth(): Int =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()

    private fun FFmpegMediaMetadataRetriever.originalHeight(): Int =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()

    private fun FFmpegMediaMetadataRetriever.orientation(): Int =
            extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).toInt()


    override fun handles(source: Uri, options: Options): Boolean = true

    override fun decode(source: Uri, outWidth: Int, outHeight: Int, options: Options): Resource<Bitmap>? {
        val retriever = FFmpegMediaMetadataRetriever()
        val downSampleStrategy = options.get(DownsampleStrategy.OPTION)
                ?: DownsampleStrategy.NONE

        val bitmap: Bitmap?

        try {
            // TODO: This won't always be a valid file descriptor..fix this.
            val dataSourceFileDescriptor = context
                    .contentResolver
                    .openFileDescriptor(source, "r")
                    ?.fileDescriptor
                    ?: return null

            retriever.setDataSource(dataSourceFileDescriptor)

            val duration = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION).toLong()

            bitmap = decodeFrame(
                    retriever
                    , (duration * 1000 * frameAtDurationPercentage).toLong()
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
            , frameTimeMicros: Long
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
                    , frameTimeMicros
                    , frameOption
                    , outWidth
                    , outHeight
                    , strategy
            )
        }

        return result ?: retriever.decodeOriginalFrame(frameTimeMicros, frameOption)
    }

    private fun decodeScaledFrame(
            retriever: FFmpegMediaMetadataRetriever
            , frameTimeMicros: Long
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
                    frameTimeMicros
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
# ffmpeg-glide-video-decoder
A [Glide](https://github.com/bumptech/glide) resource decoder powered by [FFmpegMediaMetadataRetriever](https://github.com/wseemann/FFmpegMediaMetadataRetriever). This resource decoder can be registered in AppGlideModule to load video bitmaps when the standard Android MediaMetadataRetriever fails.


## Usage

Within your derived ```AppGlideModule``` class register the decoder. For example:

```kotlin
@GlideModule()
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions().apply {
            // Get frame at 10% into the media (default 3%)
            set(FFmpegVideoDecoder.PERCENTAGE_DURATION, 0.10F)
            // OR get frame at some time (micro-seconds)
            // set(FFmpegVideoDecoder.FRAME_AT_TIME, 1000 * 1000 * 5)
        }

        builder.setDefaultRequestOptions(requestOptions)

        super.applyOptions(context, builder)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        registry.append(
                Registry.BUCKET_BITMAP
                , Uri::class.java
                , Bitmap::class.java
                , FFmpegVideoDecoder(glide.bitmapPool)
        )

        super.registerComponents(context, glide, registry)
    }
}
```

Please refer to the demo application for a more complete example.

## Configuration

Add this in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and add the following in the dependent module:

```gradle
dependencies {
    implementation 'com.github.masterwok:ffmpeg-glide-video-decoder:1.0.0'
}
```

## Projects using ffmpeg-glide-video-decoder
- [Bit Cast](https://play.google.com/store/apps/details?id=com.masterwok.bitcast)

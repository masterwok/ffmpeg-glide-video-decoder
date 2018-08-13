# ffmpeg-glide-video-decoder
A Glide resource decoder powered by [FFmpegMediaMetadataRetriever](https://github.com/wseemann/FFmpegMediaMetadataRetriever). This resource decoder can be registered in AppGlideModule to load bitmaps when Android's built in MediaMetadataRetriever fails.


## Usage

Within your derived ```AppGlideModule``` class register the decoder. For example:

```kotlin
@GlideModule()
class GlideModule : AppGlideModule() {
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

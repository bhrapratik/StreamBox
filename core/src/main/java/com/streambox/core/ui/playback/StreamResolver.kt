package com.streambox.core.ui.playback
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes

/**
 * Real apps call a backend "playback URL" endpoint (often with DRM licence info).
 * We map each contentId to a free test stream, the same id always gets the same stream.
 */
object StreamResolver {

    private val streams = listOf(
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
        "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
        "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
    )


    fun resolve(item: MediaItem): MediaItem {
        val url = streams[Math.floorMod(item.mediaId.hashCode(), streams.size)]
        return item.buildUpon()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
    }
}
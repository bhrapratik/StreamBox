package com.streambox.core.ui.playback
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes

/**
 * Real apps call a backend "playback URL" endpoint (often with DRM licence info).
 * We map each contentId to a free test stream, the same id always gets the same stream.
 */
object StreamResolver {

    private val streams = listOf(
        "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",   // Big Buck Bunny
    )


    fun resolve(item: MediaItem): MediaItem {
        val url = streams[Math.floorMod(item.mediaId.hashCode(), streams.size)]
        return item.buildUpon()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
    }
}
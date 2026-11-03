package com.streambox.core.ui.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val contentId: String = checkNotNull(savedStateHandle["contentId"])
     val title: String = savedStateHandle["title"] ?: ""
    private val artworkUrl: String? = savedStateHandle["artworkUrl"]

    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, PlaybackService::class.java)),
        ).buildAsync()

    /** UI only sees the Player interface, not MediaController/ExoPlayer. */
    private val _player = MutableStateFlow<Player?>(null)
    val player: StateFlow<Player?> = _player.asStateFlow()

    init {
        viewModelScope.launch {
            val controller = controllerFuture.await()      // binds to (and starts) the service
            if (controller.currentMediaItem?.mediaId != contentId) {
                controller.setMediaItem(
                    MediaItem.Builder()
                        .setMediaId(contentId)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(title)
                                .setArtworkUri(artworkUrl?.toUri())
                                .build()
                        )
                        .build()
                )
                controller.prepare()
            }
            controller.playWhenReady = true
            _player.value = controller
        }
    }

    override fun onCleared() {
        _player.value?.stop()
        MediaController.releaseFuture(controllerFuture)
    }

}
package com.streambox.tv.ui.player

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.streambox.core.ui.playback.PlayerViewModel
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text


@OptIn(UnstableApi::class)
@Composable
fun TvPlayerScreen(viewModel: PlayerViewModel = hiltViewModel()) {
    val player by viewModel.player.collectAsStateWithLifecycle()

    var playerView by remember { mutableStateOf<PlayerView?>(null) }
    var controlsVisible by remember { mutableStateOf(false) }

    // TV guideline: leaving the app (Home) → stop video
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { player?.pause() }

    // Back: 1st press hides the controls, 2nd press leaves the player (NavHost pops)
    BackHandler(enabled = controlsVisible) { playerView?.hideController() }

    Box(Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        val p = player
        if (p == null) {
            Text("Loading…", modifier = Modifier.align(Alignment.Center))
        } else {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        useController = true
                        controllerShowTimeoutMs = 4_000          // auto-hide after 4 s
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        setControllerVisibilityListener(
                            PlayerView.ControllerVisibilityListener { visibility ->
                                controlsVisible = visibility == View.VISIBLE
                            }
                        )
                        isFocusable = true
                        post { requestFocus() }                 // key events must reach PlayerView
                        playerView = this
                    }
                },
                update = { view -> view.player = p },
                onRelease = { view ->
                    view.player = null
                    playerView = null
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)))
                    .padding(start = 48.dp, end = 48.dp, top = 27.dp, bottom = 40.dp),   // overscan safe area
            ) {
                Text(
                    text = viewModel.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
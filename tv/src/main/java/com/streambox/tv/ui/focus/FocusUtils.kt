package com.streambox.tv.ui.focus

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/**
 * Scrolls a lazy list so the focused child's top edge sits at [parentFraction] of the container.
 * 0f = top of the list. Telia Play / Netflix style: the focused row is always in the same place.
 */
@OptIn(ExperimentalFoundationApi::class)
class PinFocusedItem(private val parentFraction: Float = 0f) : BringIntoViewSpec {
    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float {
        val target = parentFraction * containerSize
        // If the item wouldn't fit below the pin, align its bottom with the container bottom instead
        val adjustedTarget =
            if (size <= containerSize && containerSize - target < size) containerSize - size else target
        return offset - adjustedTarget
    }
}

/**
 * Holding an arrow key fires ~20 repeat events/sec. Cheap TV boxes can't render that fast,
 * so focus lags behind and "keeps moving" after the user releases the key.
 * This swallows repeats that arrive faster than [intervalMs].
 */
@Composable
fun rememberKeyRepeatThrottle(intervalMs: Long = 90L): Modifier {
    val lastRepeat = remember { longArrayOf(0L) }
    return Modifier.onPreviewKeyEvent { event ->
        val native = event.nativeKeyEvent
        if (event.type == KeyEventType.KeyDown && native.repeatCount > 0) {
            if (native.eventTime - lastRepeat[0] < intervalMs) return@onPreviewKeyEvent true // consume
            lastRepeat[0] = native.eventTime
        }
        false // let it continue to the focused card
    }
}
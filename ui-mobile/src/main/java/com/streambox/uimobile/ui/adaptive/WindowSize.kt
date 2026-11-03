package com.streambox.uimobile.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Wide = landscape and at least 600dp wide: car displays, tablets, phone landscape. */
@Composable
fun isWideLayout(): Boolean {
    val config = LocalConfiguration.current
    return config.screenWidthDp >= 600 && config.screenWidthDp > config.screenHeightDp
}
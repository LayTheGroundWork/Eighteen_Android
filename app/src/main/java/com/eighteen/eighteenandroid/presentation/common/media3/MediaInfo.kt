package com.eighteen.eighteenandroid.presentation.common.media3

import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.RepeatMode

data class MediaInfo(
    val id: String?,
    val mediaUrl: String,
    val mediaView: MediaView,
    @RepeatMode val repeatMode: Int = REPEAT_MODE_ONE
)

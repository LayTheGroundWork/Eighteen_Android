package com.eighteen.eighteenandroid.presentation.auth.signup.model

import android.graphics.Bitmap

sealed interface SignUpMedia {
    val tags: List<String>

    data class Image(val imageBitmap: Bitmap, override val tags: List<String>) : SignUpMedia
    data class Video(val thumbnailBitmap: Bitmap, override val tags: List<String>) : SignUpMedia
}
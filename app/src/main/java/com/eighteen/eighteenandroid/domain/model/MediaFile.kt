package com.eighteen.eighteenandroid.domain.model

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

sealed interface MediaFile {
    val file: File
    val type: MediaType?

    data class Image(override val file: File) : MediaFile {
        override val type: MediaType? = "image/*".toMediaTypeOrNull()
    }

    data class Video(override val file: File) : MediaFile {
        override val type: MediaType? = "video/*".toMediaTypeOrNull()
    }
}
package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.model

import android.graphics.Bitmap

sealed interface SignUpMediaItemModel {
    val isMain: Boolean
    fun areItemsTheSame(other: SignUpMediaItemModel): Boolean
    fun areContentsTheSame(other: SignUpMediaItemModel): Boolean

    object Empty : SignUpMediaItemModel {
        override val isMain: Boolean = false
        override fun areItemsTheSame(other: SignUpMediaItemModel) = other is Empty
        override fun areContentsTheSame(other: SignUpMediaItemModel) = other is Empty
    }

    data class Image(val imageBitmap: Bitmap, override val isMain: Boolean = false) :
        SignUpMediaItemModel {
        override fun areItemsTheSame(other: SignUpMediaItemModel): Boolean =
            other is Image && this.imageBitmap == other.imageBitmap

        override fun areContentsTheSame(other: SignUpMediaItemModel): Boolean =
            other is Image && this == other
    }

    data class Video(val uriString: String, override val isMain: Boolean = false) :
        SignUpMediaItemModel {
        override fun areItemsTheSame(other: SignUpMediaItemModel): Boolean =
            other is Video && this.uriString == other.uriString

        override fun areContentsTheSame(other: SignUpMediaItemModel): Boolean =
            other is Video && this == other
    }
}
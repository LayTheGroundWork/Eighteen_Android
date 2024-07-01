package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.model

import android.graphics.Bitmap

sealed interface SignUpMediaItemModel {
    fun areContentsTheSame(other: SignUpMediaItemModel): Boolean

    object Empty : SignUpMediaItemModel {
        override fun areContentsTheSame(other: SignUpMediaItemModel) = other is Empty
    }

    object RefEmpty : SignUpMediaItemModel {
        override fun areContentsTheSame(other: SignUpMediaItemModel) = other is RefEmpty
    }

    data class Image(val imageBitmap: Bitmap) : SignUpMediaItemModel {
        override fun areContentsTheSame(other: SignUpMediaItemModel): Boolean =
            other is Image && this.imageBitmap == other.imageBitmap
    }

    data class Video(val thumbnailBitmap: Bitmap) : SignUpMediaItemModel {
        override fun areContentsTheSame(other: SignUpMediaItemModel): Boolean =
            other is Video && this.thumbnailBitmap == other.thumbnailBitmap
    }
}
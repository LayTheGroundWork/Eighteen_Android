package com.eighteen.eighteenandroid.presentation.editmedia.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface EditMediaResult : Parcelable {
    val tags: List<String>

    @Parcelize
    data class Image(override val tags: List<String>, val imageBitmap: Bitmap) : EditMediaResult

    //TODO 비디오 모델 구현
    @Parcelize
    data class Video(override val tags: List<String>, val thumbnailBitmap: Bitmap) : EditMediaResult
}

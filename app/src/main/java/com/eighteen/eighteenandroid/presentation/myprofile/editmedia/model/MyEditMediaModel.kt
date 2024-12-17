package com.eighteen.eighteenandroid.presentation.myprofile.editmedia.model

import android.graphics.Bitmap

data class MyEditMediaModel(
    val mainMedia: Media? = null,
    val medias: List<Media> = emptyList()
) {
    val mainMediaOrFirst
        get() = mainMedia ?: medias.firstOrNull()

    sealed interface Media {
        class Image private constructor() : Media {
            var imageUrl: String? = null
                private set
            var bitmap: Bitmap? = null
                private set

            constructor(imageUrl: String?) : this() {
                this.imageUrl = imageUrl
            }

            constructor(bitmap: Bitmap?) : this() {
                this.bitmap = bitmap
            }
        }

        data class Video(val url: String?) : Media
    }
}
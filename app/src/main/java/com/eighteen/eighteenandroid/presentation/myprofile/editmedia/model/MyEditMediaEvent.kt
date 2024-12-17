package com.eighteen.eighteenandroid.presentation.myprofile.editmedia.model

sealed interface MyEditMediaEvent {
    val uri: String?

    data class Image(override val uri: String?) : MyEditMediaEvent

    data class Video(override val uri: String?) : MyEditMediaEvent
}

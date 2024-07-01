package com.eighteen.eighteenandroid.presentation.auth.signup.model

sealed interface SignUpEditMediaAction {
    val uriString: String

    class EditImage(override val uriString: String) : SignUpEditMediaAction
    class EditVideo(override val uriString: String) : SignUpEditMediaAction
}
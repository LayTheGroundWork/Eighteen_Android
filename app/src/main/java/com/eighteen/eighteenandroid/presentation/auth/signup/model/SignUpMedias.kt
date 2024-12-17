package com.eighteen.eighteenandroid.presentation.auth.signup.model

data class SignUpMedias(
    val mainMedia: SignUpMedia? = null,
    val medias: List<SignUpMedia> = emptyList()
) {
    val mainMediaOrFirst
        get() = mainMedia ?: medias.firstOrNull()
}

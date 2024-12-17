package com.eighteen.eighteenandroid.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)

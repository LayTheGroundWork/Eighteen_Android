package com.eighteen.eighteenandroid.data.datasource.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnsPlatformRequest(
    val instagram: String?,
    val x: String?,
    val tiktok: String?,
    val youtube: String?
)

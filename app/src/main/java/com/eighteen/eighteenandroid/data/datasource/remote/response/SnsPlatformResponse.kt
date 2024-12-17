package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SnsPlatformResponse(
    val instagram: String?,
    val x: String?,
    val tiktok: String?,
    val youtube: String?
)

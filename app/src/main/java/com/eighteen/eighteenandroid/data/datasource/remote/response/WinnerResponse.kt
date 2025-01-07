package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WinnerResponse(
    val tournamentNo: Int,
    val round: Int,
    val profileImageUrl: String
)
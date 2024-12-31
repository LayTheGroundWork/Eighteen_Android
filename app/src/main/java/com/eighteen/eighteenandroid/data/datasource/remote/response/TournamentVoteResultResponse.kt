package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TournamentVoteResultResponse(
    val rankerId: String,
    val rankerNickName: String,
    val rank: Int,
    val score: Int,
    val profileImageUrl: String
)
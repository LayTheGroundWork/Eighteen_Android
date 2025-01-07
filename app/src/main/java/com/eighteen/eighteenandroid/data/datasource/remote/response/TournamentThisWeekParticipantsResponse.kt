package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TournamentThisWeekParticipantsResponse(
    val userId: String,
    val profileImageUrl: String,
    val userName: String,
    val userSchool: String,
    val userBirth: String
)
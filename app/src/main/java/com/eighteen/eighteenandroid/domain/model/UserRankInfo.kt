package com.eighteen.eighteenandroid.domain.model

data class UserRankInfo(
    val rankerId: String,
    val rankerNickName: String,
    val rank: Int,
    val voteRate: Double,
    val profileImageUrl: String
)
package com.eighteen.eighteenandroid.domain.model

data class User(
    val userId: Int,
    val userImage: String,
    val uniqueId: String,
    val userName: String,
    val userAge: String,
    val userSchoolName: String,
    var likeStatus: Boolean
)

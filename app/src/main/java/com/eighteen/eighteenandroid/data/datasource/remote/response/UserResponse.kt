package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class UserResponse(
    val users: List<UserDto>,
    val totalPage: Int
)

data class UserDto(
    val profileImage: String,
    val uniqueId: String,
    val nickName: String,
    val birthDay: String,
    val location: String,
    val schoolName: String,
    val likeStatus: Boolean
)

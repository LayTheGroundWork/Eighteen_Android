package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "profileImage")
    val userImage: String,
    @Json(name = "uniqueId")
    val userId: String,
    @Json(name = "nickName")
    val name: String,
    @Json(name = "birthDate")
    val birthDate: String,
    val location: String,
    val schoolName: String,
    @Json(name = "likeStatus")
    val isLike: Boolean
)

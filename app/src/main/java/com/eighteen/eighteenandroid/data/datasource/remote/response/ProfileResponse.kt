package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    val id: Int,
    //TODO 미디어 응답 값 확인
    val profileImages: List<String>?,
    val likeCount: Int?,
    val nickName: String?,
    val uniqueId: String?,
    val mbti: String?,
    val introduction: String?,
    val birthDay: String?,
    val location: String?,
    val schoolName: String?,
    val snsPlatform: SnsPlatformResponse?,
    val category: String,
    val tournamentJoin: Boolean,
    val questions: List<QuestionResponse>?
)
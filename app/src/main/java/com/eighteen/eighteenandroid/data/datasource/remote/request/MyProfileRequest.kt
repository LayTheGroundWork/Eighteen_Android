package com.eighteen.eighteenandroid.data.datasource.remote.request

import com.squareup.moshi.JsonClass

//TODO 미디어 수정(서버 미구현)
@JsonClass(generateAdapter = true)
data class MyProfileRequest(
    val mediaUrl: List<String>,
    val nickName: String?,
    val schoolData: SchoolRequest?,
    val snsPlatform: SnsPlatformRequest?,
    val mbti: String?,
    val introduction: String?,
    val questions: List<QuestionRequest>
)

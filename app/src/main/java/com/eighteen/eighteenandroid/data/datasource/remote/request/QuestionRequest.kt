package com.eighteen.eighteenandroid.data.datasource.remote.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestionRequest(
    val question: String,
    val answer: String
)

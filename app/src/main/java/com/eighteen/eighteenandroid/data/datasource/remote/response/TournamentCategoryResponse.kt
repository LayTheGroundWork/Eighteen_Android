package com.eighteen.eighteenandroid.data.datasource.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TournamentCategoryResponse(
    val category: String,
    val thisWeekTournamentNo: Int,
    val winner: List<WinnerResponse>
)
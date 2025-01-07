package com.eighteen.eighteenandroid.domain.model

import com.squareup.moshi.Json

class TournamentCategory(
    val category: String,
    val thisWeekTournamentNo: Int,
    val winner: List<WinnerInfo>
)
package com.eighteen.eighteenandroid.domain.model

class RankingTournament (
    val category: String,
    val round: String,
    val participants: List<Participant>,
    val progress: Float,
    val isFinal: Boolean
)
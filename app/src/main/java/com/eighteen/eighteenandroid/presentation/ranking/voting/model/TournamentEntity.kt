package com.eighteen.eighteenandroid.presentation.ranking.voting.model

sealed class TournamentEntity {

    data class Participant(
        val id: String,
        val nickName: String,
        val school: String,
        val age: String,
        val imageUrl: String = ""
    ) : TournamentEntity()

    data class Match(
        val participant1: Participant,
        val participant2: Participant,
        var winner: Participant? = null
    ) : TournamentEntity()
}
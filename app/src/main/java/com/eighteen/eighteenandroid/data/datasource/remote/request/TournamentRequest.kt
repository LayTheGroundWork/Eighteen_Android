package com.eighteen.eighteenandroid.data.datasource.remote.request

class TournamentRequest(
    val tournamentNo: Int,
    val participantIdsOrderByRank: List<String>
)
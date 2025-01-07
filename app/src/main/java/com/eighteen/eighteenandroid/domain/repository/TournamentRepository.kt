package com.eighteen.eighteenandroid.domain.repository

import com.eighteen.eighteenandroid.domain.model.UserRankInfo

import com.eighteen.eighteenandroid.domain.model.Participant
import com.eighteen.eighteenandroid.domain.model.TournamentCategory

interface TournamentRepository {
    suspend fun getTournament(): Result<List<TournamentCategory>>

    suspend fun getThisWeekParticipants(category: String): Result<List<Participant>>

    suspend fun postTournament(
        tournamentNo: Int,
        participantIdsOrderByRank: List<String>
    ): Result<String>

    suspend fun getTournamentResult(tournamentNo: Int): Result<List<UserRankInfo>>
}
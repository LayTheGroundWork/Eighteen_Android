package com.eighteen.eighteenandroid.domain.repository

import com.eighteen.eighteenandroid.domain.model.UserRankInfo

interface TournamentRepository {
    suspend fun getTournamentResult(tournamentNo: Int): Result<List<UserRankInfo>>
}
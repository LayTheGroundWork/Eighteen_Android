package com.eighteen.eighteenandroid.data.repository

import com.eighteen.eighteenandroid.data.datasource.remote.service.TournamentService
import com.eighteen.eighteenandroid.data.mapper.TournamentMapper
import com.eighteen.eighteenandroid.data.mapper.mapper
import com.eighteen.eighteenandroid.domain.model.UserRankInfo
import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import javax.inject.Inject

class TournamentRepositoryImpl @Inject constructor(private val tournamentService: TournamentService): TournamentRepository {

    override suspend fun getTournamentResult(tournamentNo: Int): Result<List<UserRankInfo>> = runCatching {
        tournamentService.getTournamentResult(tournamentNo).mapper { response ->
            response.data?.let { resultDtoList -> TournamentMapper.asUserRankInfoList(resultDtoList) }
                ?: emptyList()
        }
    }
}
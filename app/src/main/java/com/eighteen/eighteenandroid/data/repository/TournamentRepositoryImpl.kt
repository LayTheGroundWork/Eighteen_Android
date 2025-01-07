package com.eighteen.eighteenandroid.data.repository

import com.eighteen.eighteenandroid.data.datasource.remote.request.TournamentRequest
import com.eighteen.eighteenandroid.data.datasource.remote.service.TournamentService
import com.eighteen.eighteenandroid.data.mapper.TournamentMapper
import com.eighteen.eighteenandroid.data.mapper.mapper
import com.eighteen.eighteenandroid.domain.model.Participant
import com.eighteen.eighteenandroid.domain.model.TournamentCategory
import com.eighteen.eighteenandroid.domain.model.UserRankInfo
import com.eighteen.eighteenandroid.domain.model.WinnerInfo
import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import javax.inject.Inject

class TournamentRepositoryImpl @Inject constructor(private val tournamentService: TournamentService) :
    TournamentRepository {
    override suspend fun getTournament(): Result<List<TournamentCategory>> = runCatching {
//        tournamentService.getTournamentParticipants().mapper { response ->
//            response.data?.map { tournamentResponse ->
//                TournamentMapper.asTournamentCategoryModel(
//                    tournamentCategoryResponse = tournamentResponse
//                )
//            } ?: emptyList()
//        }

        val mockData = listOf(
            TournamentCategory(
                category = "뷰티",
                thisWeekTournamentNo = 1,
                winner = listOf(
                    WinnerInfo(
                        tournamentNo = 1,
                        round = 16,
                        profileImageUrl = "https://example.com/image1.jpg"
                    ),
                    WinnerInfo(
                        tournamentNo = 1,
                        round = 8,
                        profileImageUrl = "https://example.com/image2.jpg"
                    )
                )
            ),
            TournamentCategory(
                category = "운동",
                thisWeekTournamentNo = 2,
                winner = listOf(
                    WinnerInfo(
                        tournamentNo = 2,
                        round = 16,
                        profileImageUrl = "https://example.com/image3.jpg"
                    ),
                    WinnerInfo(
                        tournamentNo = 2,
                        round = 8,
                        profileImageUrl = "https://example.com/image4.jpg"
                    )
                )
            )
        )
        // Result로 성공 데이터를 반환
        return Result.success(mockData)

    }

    override suspend fun getThisWeekParticipants(category: String): Result<List<Participant>> =
        runCatching {
            tournamentService.getThisWeekParticipants(category = category).mapper { response ->
                response.data?.map { thisWeekParticipantsResponse ->
                    TournamentMapper.asTournamentThisWeekParticipantsModel(
                        tournamentThisWeekParticipantsResponse = thisWeekParticipantsResponse
                    )
                } ?: emptyList()
            }
        }

    override suspend fun postTournament(
        tournamentNo: Int,
        participantIdsOrderByRank: List<String>
    ) = runCatching {
        val request = TournamentRequest(
            tournamentNo = tournamentNo,
            participantIdsOrderByRank = participantIdsOrderByRank
        )

        tournamentService.submitTournamentResults(tournamentRequest = request).mapper {
            it.data ?: ""
        }
    }

    override suspend fun getTournamentResult(tournamentNo: Int): Result<List<UserRankInfo>> =
        runCatching {
            tournamentService.getTournamentResult(tournamentNo).mapper { response ->
                response.data?.let { resultDtoList ->
                    TournamentMapper.asUserRankInfoList(
                        resultDtoList
                    )
                }
                    ?: emptyList()
            }
        }
}
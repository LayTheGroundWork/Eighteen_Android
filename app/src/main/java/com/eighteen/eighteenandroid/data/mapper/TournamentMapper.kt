package com.eighteen.eighteenandroid.data.mapper

import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentCategoryResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentThisWeekParticipantsResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentVoteResultResponse
import com.eighteen.eighteenandroid.domain.model.Participant
import com.eighteen.eighteenandroid.domain.model.TournamentCategory
import com.eighteen.eighteenandroid.domain.model.UserRankInfo
import com.eighteen.eighteenandroid.domain.model.WinnerInfo

object TournamentMapper {
//    fun TournamentCategoryResponse.toTournamentCategory() = TournamentCategory(
//        category = category,
//        thisWeekTournamentNo = thisWeekTournamentNo,
//        winner = winner.map { WinnerInfo(it.tournamentNo, it.round, it.profileImageUrl) }
//    )

    fun asTournamentCategoryModel(tournamentCategoryResponse: TournamentCategoryResponse) =
        TournamentCategory(
            category = tournamentCategoryResponse.category,
            thisWeekTournamentNo = tournamentCategoryResponse.thisWeekTournamentNo,
            winner = tournamentCategoryResponse.winner.map {
                WinnerInfo(
                    it.tournamentNo,
                    it.round,
                    it.profileImageUrl
                )
            }
        )

    fun asTournamentThisWeekParticipantsModel(tournamentThisWeekParticipantsResponse: TournamentThisWeekParticipantsResponse) =
        Participant(
            id = tournamentThisWeekParticipantsResponse.userId,
            imgUrl = tournamentThisWeekParticipantsResponse.profileImageUrl,
            name = tournamentThisWeekParticipantsResponse.userName,
            school = tournamentThisWeekParticipantsResponse.userSchool,
            age = tournamentThisWeekParticipantsResponse.userBirth
        )

    fun asUserRankInfoList(response: List<TournamentVoteResultResponse>): List<UserRankInfo> {
        val sumOfVoteCount = response.sumOf { it.score }

        return response.map {
            UserRankInfo(
                rankerId = it.rankerId,
                rankerNickName = it.rankerNickName,
                rank = it.rank,
                profileImageUrl = it.profileImageUrl,
                voteRate = (it.score / sumOfVoteCount.toDouble()) * 100
            )
        }
    }
}
package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import javax.inject.Inject

class SubmitTournamentResultsUseCase @Inject constructor(private val repository: TournamentRepository) {
    suspend operator fun invoke(
        tournamentNo: Int,
        participantIdsOrderByRank: List<String>
    ) = repository.postTournament(
        tournamentNo = tournamentNo,
        participantIdsOrderByRank = participantIdsOrderByRank
    )
}
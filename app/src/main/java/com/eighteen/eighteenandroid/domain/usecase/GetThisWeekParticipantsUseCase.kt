package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import javax.inject.Inject

class GetThisWeekParticipantsUseCase @Inject constructor(private val repository: TournamentRepository) {
    suspend operator fun invoke(category: String) =
        repository.getThisWeekParticipants(category = category)
}
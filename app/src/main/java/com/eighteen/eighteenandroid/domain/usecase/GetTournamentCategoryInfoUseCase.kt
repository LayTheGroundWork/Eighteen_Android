package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import javax.inject.Inject

class GetTournamentCategoryInfoUseCase @Inject constructor(private val repository: TournamentRepository) {
    suspend operator fun invoke() = repository.getTournament()
}
package com.eighteen.eighteenandroid.domain.di

import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import com.eighteen.eighteenandroid.domain.usecase.GetTournamentRankingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RankingUseCaseModule {
    @Provides
    @Singleton
    fun provideTournamentRankingUseCase(repository: TournamentRepository) = GetTournamentRankingUseCase(repository)
}
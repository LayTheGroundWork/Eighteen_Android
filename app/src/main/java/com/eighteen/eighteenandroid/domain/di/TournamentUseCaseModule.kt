package com.eighteen.eighteenandroid.domain.di

import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import com.eighteen.eighteenandroid.domain.usecase.GetThisWeekParticipantsUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetTournamentCategoryInfoUseCase
import com.eighteen.eighteenandroid.domain.usecase.SubmitTournamentResultsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TournamentUseCaseModule {
    @Provides
    @Singleton
    fun provideGetTournamentParticipantsUseCase(repository: TournamentRepository) =
        GetTournamentCategoryInfoUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideGetThisWeekParticipantsUseCase(repository: TournamentRepository) =
        GetThisWeekParticipantsUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideSubmitTournamentResultsUseCase(repository: TournamentRepository) =
        SubmitTournamentResultsUseCase(repository = repository)
}
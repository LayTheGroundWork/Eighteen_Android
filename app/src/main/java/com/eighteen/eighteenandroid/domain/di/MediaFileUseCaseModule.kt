package com.eighteen.eighteenandroid.domain.di

import com.eighteen.eighteenandroid.domain.repository.MediaFileRepository
import com.eighteen.eighteenandroid.domain.usecase.UploadMediaFilesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaFileUseCaseModule {
    @Provides
    @Singleton
    fun provideUploadMediaFilesUseCase(repository: MediaFileRepository) =
        UploadMediaFilesUseCase(repository = repository)
}
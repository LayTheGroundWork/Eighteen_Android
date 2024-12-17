package com.eighteen.eighteenandroid.domain.di

import com.eighteen.eighteenandroid.domain.repository.MyPageRepository
import com.eighteen.eighteenandroid.domain.repository.UserRepository
import com.eighteen.eighteenandroid.domain.usecase.CheckIdDuplicationUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetMyProfileUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetUserDetailInfoUseCase
import com.eighteen.eighteenandroid.domain.usecase.LoginUseCase
import com.eighteen.eighteenandroid.domain.usecase.SignUpUseCase
import com.eighteen.eighteenandroid.domain.usecase.UserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserUseCaseModule {
    @Provides
    @Singleton
    fun provideUserUseCase(repository: UserRepository): UserUseCase =
        UserUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideUserDetailInfoUseCase(repository: UserRepository): GetUserDetailInfoUseCase =
        GetUserDetailInfoUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideCheckIdDuplicationUseCase(repository: UserRepository) =
        CheckIdDuplicationUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideSignUpUseCase(repository: UserRepository) = SignUpUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideMyProfileUseCase(repository: MyPageRepository) =
        GetMyProfileUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: UserRepository) = LoginUseCase(repository = repository)
}
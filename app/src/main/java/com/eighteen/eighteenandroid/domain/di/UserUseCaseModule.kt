package com.eighteen.eighteenandroid.domain.di

import com.eighteen.eighteenandroid.domain.repository.MyPageRepository
import com.eighteen.eighteenandroid.domain.repository.UserRepository
import com.eighteen.eighteenandroid.domain.usecase.CheckIdDuplicationUseCase
import com.eighteen.eighteenandroid.domain.usecase.DeleteUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetAnotherUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetMyProfileUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetPopularUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetUserDetailInfoUseCase
import com.eighteen.eighteenandroid.domain.usecase.LoginUseCase
import com.eighteen.eighteenandroid.domain.usecase.SignOutUseCase
import com.eighteen.eighteenandroid.domain.usecase.SignUpUseCase
import com.eighteen.eighteenandroid.domain.usecase.UserLikeUseCase
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

    @Provides
    @Singleton
    fun provideSignOutUseCase(repository: UserRepository) = SignOutUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideDeleteUserUseCase(repository: UserRepository) =
        DeleteUserUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideGetAnotherUserUseCase(repository: UserRepository) = GetAnotherUserUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideGetPopularUserUseCase(repository: UserRepository) = GetPopularUserUseCase(repository = repository)

    @Provides
    @Singleton
    fun provideUserLikeUseCase(repository: UserRepository) = UserLikeUseCase(repository = repository)
}
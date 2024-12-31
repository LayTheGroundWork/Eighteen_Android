package com.eighteen.eighteenandroid.data.repository.di

import com.eighteen.eighteenandroid.data.repository.MessageRepositoryImpl
import com.eighteen.eighteenandroid.data.repository.MyPageRepositoryImpl
import com.eighteen.eighteenandroid.data.repository.SchoolRepositoryImpl
import com.eighteen.eighteenandroid.data.repository.TournamentRepositoryImpl
import com.eighteen.eighteenandroid.data.repository.UserRepositoryImpl
import com.eighteen.eighteenandroid.data.repository.mock.MockChatRepositoryImpl
import com.eighteen.eighteenandroid.domain.repository.ChatRepository
import com.eighteen.eighteenandroid.domain.repository.MessageRepository
import com.eighteen.eighteenandroid.domain.repository.MyPageRepository
import com.eighteen.eighteenandroid.domain.repository.SchoolRepository
import com.eighteen.eighteenandroid.domain.repository.TournamentRepository
import com.eighteen.eighteenandroid.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindSchoolRepository(schoolRepositoryImpl: SchoolRepositoryImpl): SchoolRepository

    //fixme mock repository 사용 중
    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepositoryImpl: MockChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindMyPageRepository(myPageRepositoryImpl: MyPageRepositoryImpl): MyPageRepository

    @Binds
    @Singleton
    abstract fun bindTournamentRepository(tournamentRepositoryImpl: TournamentRepositoryImpl): TournamentRepository
}
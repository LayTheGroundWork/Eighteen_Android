package com.eighteen.eighteenandroid.data.datasource.remote.di

import com.eighteen.eighteenandroid.data.datasource.remote.service.ChatService
import com.eighteen.eighteenandroid.data.datasource.remote.service.MessageService
import com.eighteen.eighteenandroid.data.datasource.remote.service.MyPageService
import com.eighteen.eighteenandroid.data.datasource.remote.service.SchoolService
import com.eighteen.eighteenandroid.data.datasource.remote.service.TokenReissueService
import com.eighteen.eighteenandroid.data.datasource.remote.service.TournamentService
import com.eighteen.eighteenandroid.data.datasource.remote.service.UserService
import com.eighteen.eighteenandroid.data.di.ApiModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideMessageService(@ApiModule.QualifierRetrofit retrofit: Retrofit): MessageService =
        retrofit.create(MessageService::class.java)

    @Singleton
    @Provides
    fun provideUserService(@ApiModule.QualifierRetrofit retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Singleton
    @Provides
    fun provideSchoolService(@ApiModule.QualifierRetrofit retrofit: Retrofit): SchoolService =
        retrofit.create(SchoolService::class.java)

    @Singleton
    @Provides
    fun provideChatService(@ApiModule.QualifierRetrofit retrofit: Retrofit): ChatService =
        retrofit.create(ChatService::class.java)

    @Singleton
    @Provides
    fun provideMyPageService(@ApiModule.QualifierRetrofit retrofit: Retrofit): MyPageService =
        retrofit.create(MyPageService::class.java)

    @Singleton
    @Provides
    fun provideTokenReissueService(@ApiModule.QualifierTokenReissueRetrofit retrofit: Retrofit): TokenReissueService =
        retrofit.create(TokenReissueService::class.java)

    @Singleton
    @Provides
    fun provideTournamentService(@ApiModule.QualifierRetrofit retrofit: Retrofit): TournamentService =
        retrofit.create(TournamentService::class.java)
}

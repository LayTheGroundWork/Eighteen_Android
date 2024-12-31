package com.eighteen.eighteenandroid.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eighteen.eighteenandroid.BuildConfig
import com.eighteen.eighteenandroid.data.datasource.remote.service.TokenReissueService
import com.eighteen.eighteenandroid.data.retrofit.AuthHeaderInterceptor
import com.eighteen.eighteenandroid.data.retrofit.AuthTokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL: String = BuildConfig.BASE_URL
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class QualifierRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class QualifierTokenReissueRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class QualifierS3Retrofit

    @Singleton
    @Provides
    @QualifierRetrofit
    fun provideRetrofit(
        authHeaderInterceptor: AuthHeaderInterceptor,
        authenticator: AuthTokenAuthenticator
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
            chain.proceed(request.build())
        }.addNetworkInterceptor(loggingInterceptor).addInterceptor(authHeaderInterceptor)
            .authenticator(authenticator).build()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).client(
                okHttpClient
            ).build()
    }

    @Singleton
    @Provides
    @QualifierTokenReissueRetrofit
    fun provideTokenReissueRetrofit(
        authHeaderInterceptor: AuthHeaderInterceptor
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
            chain.proceed(request.build())
        }.addNetworkInterceptor(loggingInterceptor).addInterceptor(authHeaderInterceptor).build()
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).client(
                okHttpClient
            ).build()
    }

    @Singleton
    @Provides
    @QualifierS3Retrofit
    fun provideS3Retrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
            chain.proceed(request.build())
        }.addNetworkInterceptor(loggingInterceptor).build()
        return Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient).build()
    }


    @Singleton
    @Provides
    fun provideAuthHeaderInterceptor(preferenceDatastore: DataStore<Preferences>) =
        AuthHeaderInterceptor(preferenceDatastore)

    @Singleton
    @Provides
    fun provideAuthTokenAuthenticator(
        preferenceDatastore: DataStore<Preferences>,
        tokenReissueService: TokenReissueService
    ) = AuthTokenAuthenticator(
        preferenceDatastore = preferenceDatastore,
        tokenReissueService = tokenReissueService
    )
}
package com.eighteen.eighteenandroid.data.retrofit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.eighteen.eighteenandroid.common.safeLet
import com.eighteen.eighteenandroid.data.ACCESS_TOKEN_PREFERENCE_KEY
import com.eighteen.eighteenandroid.data.HEADER_AUTHORIZATION
import com.eighteen.eighteenandroid.data.HEADER_REFRESH
import com.eighteen.eighteenandroid.data.REFRESH_TOKEN_PREFERENCE_KEY
import com.eighteen.eighteenandroid.domain.model.AuthToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthHeaderInterceptor @Inject constructor(
    private val preferenceDatastore: DataStore<Preferences>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken = runBlocking {
            preferenceDatastore.data.map {
                val accessToken = it[ACCESS_TOKEN_PREFERENCE_KEY]
                val refreshToken = it[REFRESH_TOKEN_PREFERENCE_KEY]
                safeLet(accessToken, refreshToken) { access, refresh ->
                    AuthToken(accessToken = access, refreshToken = refresh)
                }
            }.first()
        }
        val request = chain.request().newBuilder().apply {
            authToken?.let {
                addHeader(HEADER_AUTHORIZATION, it.accessToken)
                addHeader(HEADER_REFRESH, it.refreshToken)
            }
        }.build()
        return chain.proceed(request)
    }
}
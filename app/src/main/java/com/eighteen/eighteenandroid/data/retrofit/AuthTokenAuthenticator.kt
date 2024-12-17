package com.eighteen.eighteenandroid.data.retrofit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eighteen.eighteenandroid.common.safeLet
import com.eighteen.eighteenandroid.data.ACCESS_TOKEN_PREFERENCE_KEY
import com.eighteen.eighteenandroid.data.HEADER_AUTHORIZATION
import com.eighteen.eighteenandroid.data.HEADER_REFRESH
import com.eighteen.eighteenandroid.data.REFRESH_TOKEN_PREFERENCE_KEY
import com.eighteen.eighteenandroid.data.datasource.remote.service.TokenReissueService
import com.eighteen.eighteenandroid.domain.model.AuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthTokenAuthenticator @Inject constructor(
    private val preferenceDatastore: DataStore<Preferences>,
    private val tokenReissueService: TokenReissueService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val authToken = runBlocking {
            preferenceDatastore.data.map {
                val accessToken = it[ACCESS_TOKEN_PREFERENCE_KEY]
                val refreshToken = it[REFRESH_TOKEN_PREFERENCE_KEY]
                safeLet(accessToken, refreshToken) { access, refresh ->
                    AuthToken(accessToken = access, refreshToken = refresh)
                }
            }.first()
        }
        if (authToken == null) {
            response.close()
            return null
        }
        val newToken = runBlocking { tokenReissueService.postTokenReissue() }
        val nAccessToken = newToken.headers()[HEADER_AUTHORIZATION]
        val nRefreshToken = newToken.headers()[HEADER_REFRESH]
        if (newToken.isSuccessful.not() || nAccessToken == null || nRefreshToken == null) {
            CoroutineScope(Dispatchers.IO).launch {
                preferenceDatastore.edit {
                    it.remove(ACCESS_TOKEN_PREFERENCE_KEY)
                    it.remove(REFRESH_TOKEN_PREFERENCE_KEY)
                }
            }
            return null
        }
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDatastore.edit {
                it[ACCESS_TOKEN_PREFERENCE_KEY] = nAccessToken
                it[REFRESH_TOKEN_PREFERENCE_KEY] = nRefreshToken
            }
        }
        return response.request.newBuilder().headers(
            Headers.headersOf(
                HEADER_AUTHORIZATION, nAccessToken,
                HEADER_REFRESH, nRefreshToken
            )
        ).build()
    }
}
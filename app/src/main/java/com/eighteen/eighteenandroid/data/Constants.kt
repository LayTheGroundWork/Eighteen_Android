package com.eighteen.eighteenandroid.data

import androidx.datastore.preferences.core.stringPreferencesKey

const val AUTH_PREFERENCE = "auth_preference"
val ACCESS_TOKEN_PREFERENCE_KEY = stringPreferencesKey("access_token_preference_key")
val REFRESH_TOKEN_PREFERENCE_KEY = stringPreferencesKey("refresh_token_preference_key")
const val HEADER_AUTHORIZATION = "Authorization"
const val HEADER_REFRESH = "Refresh"
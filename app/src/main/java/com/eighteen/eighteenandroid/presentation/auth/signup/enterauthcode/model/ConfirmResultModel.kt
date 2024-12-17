package com.eighteen.eighteenandroid.presentation.auth.signup.enterauthcode.model

import com.eighteen.eighteenandroid.domain.model.AuthToken

sealed interface ConfirmResultModel {
    object LoginFailed : ConfirmResultModel
    data class LoginSuccess(val authToken: AuthToken) : ConfirmResultModel
}
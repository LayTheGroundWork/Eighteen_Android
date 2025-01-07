package com.eighteen.eighteenandroid.presentation.auth.signup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class LoginType : Parcelable {
    SIGNUP,
    LOGIN
}
package com.eighteen.eighteenandroid.domain.repository

import com.eighteen.eighteenandroid.domain.model.AuthToken
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.model.SignUpInfo
import com.eighteen.eighteenandroid.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUserData(): Result<List<User>>
    suspend fun fetchUserDetailInfo(id: String): Result<Profile>
    suspend fun postSignUp(signUpInfo: SignUpInfo): Result<AuthToken>
    suspend fun checkIdDuplication(uniqueId: String): Result<Boolean>
    fun getTokenFlow(): Flow<AuthToken?>
    suspend fun saveToken(authToken: AuthToken)
    suspend fun login(phoneNumber: String): Result<AuthToken>
}
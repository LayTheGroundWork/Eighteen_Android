package com.eighteen.eighteenandroid.domain.repository

import com.eighteen.eighteenandroid.domain.model.AuthToken
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.model.SignUpInfo
import com.eighteen.eighteenandroid.domain.model.UserUseCaseModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUserDetailInfo(id: String): Result<Profile>
    suspend fun postSignUp(signUpInfo: SignUpInfo): Result<AuthToken>
    suspend fun checkIdDuplication(uniqueId: String): Result<Boolean>
    fun getTokenFlow(): Flow<AuthToken?>
    suspend fun saveToken(authToken: AuthToken)
    suspend fun login(phoneNumber: String): Result<AuthToken>
    suspend fun fetchAllUser(userType: String, page: Int): Result<UserUseCaseModel>
    suspend fun fetchCategoryUser(userType: String, category: String, page: Int): Result<UserUseCaseModel>
    suspend fun postLikeUser(likedId: Int): Result<String>
    suspend fun postLikeCancelUser(likedId: Int): Result<String>
}
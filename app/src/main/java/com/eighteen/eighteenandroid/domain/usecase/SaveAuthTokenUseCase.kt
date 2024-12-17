package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.model.AuthToken
import com.eighteen.eighteenandroid.domain.repository.UserRepository
import javax.inject.Inject

class SaveAuthTokenUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(accessToken: String, refreshToken: String) {
        invoke(AuthToken(accessToken, refreshToken))
    }

    suspend operator fun invoke(authToken: AuthToken) {
        repository.saveToken(authToken = authToken)
    }
}
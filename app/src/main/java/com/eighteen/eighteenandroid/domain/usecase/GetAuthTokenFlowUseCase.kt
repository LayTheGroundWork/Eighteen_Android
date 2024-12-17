package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.UserRepository
import javax.inject.Inject

class GetAuthTokenFlowUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke() = repository.getTokenFlow()
}
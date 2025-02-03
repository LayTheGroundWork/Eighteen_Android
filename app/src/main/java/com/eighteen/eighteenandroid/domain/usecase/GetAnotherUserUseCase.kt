package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.UserRepository
import javax.inject.Inject

class GetAnotherUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(category: String, userType: String, page: Int) = repository.getAnotherUser(userType, category, page)
}
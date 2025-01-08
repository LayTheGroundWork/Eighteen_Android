package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(category: String, userType: String, page: Int) = repository.fetchCategoryUser(userType, category, page)
}
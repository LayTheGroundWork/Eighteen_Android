package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.MyPageRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(private val repository: MyPageRepository) {
    suspend operator fun invoke() = repository.getMyPageProfile()
}
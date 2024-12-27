package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.repository.UserRepository
import javax.inject.Inject

class UserLikeUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(isLike: Boolean, likedId: Int) =
        if(isLike) {
            repository.postLikeUser(likedId)
        } else {
            repository.postLikeCancelUser(likedId)
        }
}
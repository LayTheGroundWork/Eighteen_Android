package com.eighteen.eighteenandroid.domain.model

data class UserUseCaseModel(
    val users: List<User>,
    val totalPageCount: Int
)
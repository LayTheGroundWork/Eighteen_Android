package com.eighteen.eighteenandroid.presentation.profiledetail.model

sealed class ProfileDetailModel {
    data class ProfileInfo(
        val name: String,
        val age: Int,
        val school: String,
        val profileImageUrl: ProfileImages) : ProfileDetailModel()

    data class ProfileImages(
        val imageUrl: List<String>
    ) : ProfileDetailModel()

    data class Like(
        val likeCount: Int
    ) : ProfileDetailModel()

    data class Introduction(
        val personalityType: String,
        val introductionText: String
    ) : ProfileDetailModel()

    data class Qna(
        val question: String,
        val answer: String
    ) : ProfileDetailModel()
}

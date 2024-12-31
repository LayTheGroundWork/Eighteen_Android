package com.eighteen.eighteenandroid.presentation.myprofile.model

import com.eighteen.eighteenandroid.domain.model.Qna
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.domain.model.SnsInfo

sealed interface MyProfileItem {
    fun areItemsTheSame(other: MyProfileItem): Boolean
    fun areContentsTheSame(other: MyProfileItem): Boolean
    data class Profile(
        val id: String,
        val nickName: String,
        val profileUrl: String?,
        val school: School,
        val age: Int,
        val badgeCount: Int? = null,
        val teenDescription: String? = null
    ) : MyProfileItem {
        override fun areItemsTheSame(other: MyProfileItem) =
            other is Profile && this == other

        override fun areContentsTheSame(other: MyProfileItem) =
            other is Profile && this == other
    }


    data class Link(val snsInfoList: List<SnsInfo>) : MyProfileItem {
        override fun areItemsTheSame(other: MyProfileItem) =
            other is Link && this == other

        override fun areContentsTheSame(other: MyProfileItem) =
            other is Link && this == other
    }

    data class Introduce(val mbti: String?, val introduction: String?) : MyProfileItem {
        override fun areItemsTheSame(other: MyProfileItem) =
            other is Introduce && this == other

        override fun areContentsTheSame(other: MyProfileItem) =
            other is Introduce && this == other
    }

    data class TenOfQna(val qnas: List<Qna>, val isExpanded: Boolean = false) : MyProfileItem {
        override fun areItemsTheSame(other: MyProfileItem) =
            other is TenOfQna && this == other

        override fun areContentsTheSame(other: MyProfileItem) =
            other is TenOfQna && this == other
    }
}
package com.eighteen.eighteenandroid.presentation.myprofile

import androidx.lifecycle.ViewModel
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.presentation.myprofile.model.MyProfileItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyProfileViewModel : ViewModel() {
    private val _myProfileItemsStateFlow = MutableStateFlow<List<MyProfileItem>>(emptyList())
    val myProfileItemsStateFlow = _myProfileItemsStateFlow.asStateFlow()

    fun setProfile(profile: Profile) {
        _myProfileItemsStateFlow.value = asProfileItems(profile = profile)
    }

    fun toggleQnaExpanded() {
        _myProfileItemsStateFlow.value = myProfileItemsStateFlow.value.toMutableList().apply {
            replaceAll {
                if (it is MyProfileItem.TenOfQna) it.copy(isExpanded = !it.isExpanded) else it
            }
        }
    }

    private fun asProfileItems(profile: Profile): List<MyProfileItem> {
        with(profile) {
            val profileItem = MyProfileItem.Profile(
                id = id,
                nickName = nickName,
                age = age,
                profileUrl = medias.firstOrNull()?.url,
                school = school,
                badgeCount = badgeCount,
                teenDescription = teenDescription
            )
            val linkItem = MyProfileItem.Link(snsInfoList = snsInfoList)
            val introduceItem =
                MyProfileItem.Introduce(mbti = mbti?.mbtiString, introduction = introduction)
            val qnaItem = MyProfileItem.TenOfQna(qnas = qna)
            return listOf(profileItem, linkItem, introduceItem, qnaItem)
        }
    }
}
package com.eighteen.eighteenandroid.presentation.auth.signup.selecttag

import androidx.lifecycle.ViewModel
import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.presentation.auth.signup.selecttag.model.SignUpTagModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpSelectTagViewModel : ViewModel() {
    private val _signUpTagModelsStateFlow =
        MutableStateFlow(tags.map { SignUpTagModel(isSelected = false, tag = it) })
    val signUpModelStateFlow = _signUpTagModelsStateFlow.asStateFlow()
    val selectedTag get() = signUpModelStateFlow.value.firstOrNull { it.isSelected }

    fun selectTag(tag: Tag) {
        _signUpTagModelsStateFlow.value =
            tags.map { SignUpTagModel(isSelected = tag == it, tag = it) }
    }

    fun clear() {
        _signUpTagModelsStateFlow.value = tags.map { SignUpTagModel(isSelected = false, tag = it) }
    }

    companion object {
        private val tags = listOf(Tag.BEAUTY, Tag.SPORT, Tag.STUDY, Tag.ART, Tag.GAME, Tag.ETC)
    }
}
package com.eighteen.eighteenandroid.presentation.auth.signup

import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.domain.model.AuthToken
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpEditMediaAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpMedias
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpPage
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.livedata.Event
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

/**
 * 회원가입 과정 중 받는 정보들을 저장 혹은 child에서 parent에 event를 전달하기 위한 interface
 */
interface SignUpViewModelContentInterface {
    var phoneNumber: String
    var id: String
    var nickName: String
    var birth: Calendar?
    var school: School?
    var tag: Tag?
    val mediasStateFlow: StateFlow<SignUpMedias>
    val signUpResultStateFlow: StateFlow<ModelState<AuthToken>>
    val pageClearEvent: StateFlow<Event<SignUpPage>>
    fun setNextButtonModel(signUpNextButtonModel: SignUpNextButtonModel)
    fun setEditMediaAction(editMediaAction: SignUpEditMediaAction)
    fun clearMediaResultStateFlow()
    fun actionOpenWebViewFragment(url: String)
    fun requestSignUp()
    fun setPageClearEvent(page: SignUpPage)
    fun removeMedia(position: Int)
    fun setMainMedia(position: Int)
    fun requestLogin(authToken: AuthToken)
}
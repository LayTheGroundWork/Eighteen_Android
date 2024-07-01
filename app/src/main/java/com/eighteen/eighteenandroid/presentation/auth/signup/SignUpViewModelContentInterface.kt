package com.eighteen.eighteenandroid.presentation.auth.signup

import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpEditMediaAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpMedia
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

/**
 * 회원가입 과정 중 받는 정보들을 저장 혹은 child에서 parent에 event를 전달하기 위한 interface
 */
interface SignUpViewModelContentInterface {
    var id: String
    var nickName: String
    var birth: Date
    var school: String
    val mediasStateFlow: StateFlow<List<SignUpMedia>>
    fun setNextButtonModel(signUpNextButtonModel: SignUpNextButtonModel)
    fun setEditMediaAction(editMediaAction: SignUpEditMediaAction)
    fun clearMediaResultStateFlow()
}
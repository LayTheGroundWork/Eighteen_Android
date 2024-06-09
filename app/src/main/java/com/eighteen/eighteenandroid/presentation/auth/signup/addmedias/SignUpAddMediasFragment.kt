package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias

import com.eighteen.eighteenandroid.databinding.FragmentSignUpAddMediasBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel

class SignUpAddMediasFragment :
    BaseSignUpContentFragment<FragmentSignUpAddMediasBinding>(FragmentSignUpAddMediasBinding::inflate) {
    override val onMoveNextPageAction: () -> Unit = {
        //TODO 구현
    }
    override val progress: Int = 100
    override val nextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = true,
        size = SignUpNextButtonModel.Size.NORMAL,
        buttonText = SignUpNextButtonModel.ButtonText.PASS
    )

    override fun initView() {

    }

}
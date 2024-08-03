package com.eighteen.eighteenandroid.presentation.auth.signup.completed

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eighteen.eighteenandroid.databinding.FragmentSignUpCompletedBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import kotlinx.coroutines.launch

class SignUpCompletedFragment :
    BaseSignUpContentFragment<FragmentSignUpCompletedBinding>(FragmentSignUpCompletedBinding::inflate) {
    override val onMoveNextPageAction: () -> Unit = {
        signUpViewModelContentInterface.requestSignUp()
    }
    override val progress: Int? = null
    override val signUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = true,
        size = SignUpNextButtonModel.Size.FULL,
        buttonText = SignUpNextButtonModel.ButtonText.START
    )

    override fun initView() {
        initStateFlow()
    }

    private fun initStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModelContentInterface.signUpResultStateFlow.collect {
                    when (it) {
                        is ModelState.Loading -> {
                            Log.d("SignUpCompletedFragment", "loading")
                            //TODO 로딩 처리
                        }
                        is ModelState.Error -> {
                            Log.d("SignUpCompletedFragment", "error $it")
                            //TODO 에러처리
                        }
                        else -> {
                            Log.d("SignUpCompletedFragment", "Empty or Success")
                            //TODO 로딩, 에러 뷰 invisible
                        }
                    }
                }
            }
        }
    }
}
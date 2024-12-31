package com.eighteen.eighteenandroid.presentation.auth.signup.enterphonenumber

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpEnterPhoneNumberBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpPage
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpStatusEvent
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.clearFocus
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.hideKeyboardAndRemoveCurrentFocus
import dagger.hilt.android.AndroidEntryPoint

/**
 * 휴대폰 번호 입력 페이지
 */
@AndroidEntryPoint
class SignUpEnterPhoneNumberFragment :
    BaseSignUpContentFragment<FragmentSignUpEnterPhoneNumberBinding>(
        FragmentSignUpEnterPhoneNumberBinding::inflate
    ) {
    override val onMovePrevPageAction: () -> Unit = {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
    override val onMoveNextPageAction: () -> Unit = {
        findNavController().navigate(R.id.action_fragmentSignUpEnterPhoneNumber_to_fragmentSignUpEnterAuthCode)
    }
    override val progress: Int? = null
    override val signUpNextButtonModel: SignUpNextButtonModel =
        SignUpNextButtonModel(isVisible = false)

    private val signUpEnterPhoneNumberViewModel by viewModels<SignUpEnterPhoneNumberViewModel>()

    override fun initView() = bind {
        initStateFlow()
        root.setOnClickListener {
            hideKeyboardAndRemoveCurrentFocus()
        }
        tvBtnGetAuthCode.setOnClickListener {
            signUpEnterPhoneNumberViewModel.requestSendMessage(phoneNumber = etInput.text.toString())
        }
        ivBtnClear.setOnClickListener {
            etInput.setText("")
        }
        etInput.setOnEditorActionListener { _, actionId, event ->
            if ((event?.keyCode == KeyEvent.KEYCODE_ENTER) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                clearFocus()
            }
            false
        }
    }

    private fun initStateFlow() {
        collectInLifecycle(signUpEnterPhoneNumberViewModel.sendMessageResultStateFlow) {
            when (it) {
                is ModelState.Loading -> {
                    signUpViewModelContentInterface.sendSignUpStatusEvent(event = SignUpStatusEvent.LOADING)
                }
                is ModelState.Success -> {
                    signUpViewModelContentInterface.sendSignUpStatusEvent(event = SignUpStatusEvent.INVISIBLE)
                    signUpViewModelContentInterface.phoneNumber = it.data ?: ""
                    onMoveNextPageAction.invoke()
                }
                is ModelState.Error -> {
                    signUpViewModelContentInterface.sendSignUpStatusEvent(event = SignUpStatusEvent.ERROR_DIALOG)
                }
                is ModelState.Empty -> {
                    //do nothing
                }
            }
        }

        collectInLifecycle(signUpViewModelContentInterface.pageClearEvent) {
            if (it.peekContent() == SignUpPage.ENTER_PHONE_NUMBER) {
                it.getContentIfNotHandled()?.run {
                    signUpViewModelContentInterface.phoneNumber = ""
                    binding.etInput.setText("")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        signUpViewModelContentInterface.phoneNumber = binding.etInput.text.toString()
    }

    override fun onDestroyView() {
        signUpEnterPhoneNumberViewModel.clear()
        super.onDestroyView()
    }
}
package com.eighteen.eighteenandroid.presentation.auth.signup.enterid

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpEnterIdBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.enterid.model.SignUpEnterIdStatus
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpPage
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpStatusEvent
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpEnterIdFragment :
    BaseSignUpContentFragment<FragmentSignUpEnterIdBinding>(FragmentSignUpEnterIdBinding::inflate) {
    override val onMovePrevPageAction: () -> Unit = {
        signUpViewModelContentInterface.setPageClearEvent(page = SignUpPage.TERMS_OF_SERVICE)
        super.onMovePrevPageAction.invoke()
    }
    override val onMoveNextPageAction: () -> Unit = {
        signUpEnterIdViewModel.checkIdValidation()
    }
    override val progress: Int = 20
    override val signUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = false,
        size = SignUpNextButtonModel.Size.NORMAL,
        buttonText = SignUpNextButtonModel.ButtonText.NEXT
    )

    private val signUpEnterIdViewModel by viewModels<SignUpEnterIdViewModel>()

    override fun initView() {
        bind {
            etInput.addTextChangedListener {
                signUpEnterIdViewModel.setInputText(input = it?.toString() ?: "")
            }
            etInput.setText(signUpViewModelContentInterface.id)
        }
        initStateFlow()
    }

    private fun initStateFlow() {
        collectInLifecycle(signUpEnterIdViewModel.signUpEnterIdModel) {
            bind {
                when (it.status) {
                    is SignUpEnterIdStatus.Error -> {
                        tvErrorMessage.isVisible = true
                        tvErrorMessage.text = resources.getString(it.status.errorStringRes)
                        etInput.setBackgroundResource(R.drawable.bg_rect_stroke_w2_red_r10)
                    }
                    else -> {
                        tvErrorMessage.isVisible = false
                        etInput.setBackgroundResource(R.drawable.bg_text_field)
                    }
                }
                val countText =
                    "${it.inputString.length}/${resources.getInteger(R.integer.sign_up_enter_id_max_length)}"
                tvCount.text = countText
                signUpViewModelContentInterface.setNextButtonModel(
                    signUpNextButtonModel = signUpNextButtonModel.copy(isEnabled = it.status is SignUpEnterIdStatus.Success)
                )
            }
        }

        collectInLifecycle(signUpEnterIdViewModel.checkIdValidationEventStateFlow) {
            when (it) {
                is ModelState.Loading -> {
                    signUpViewModelContentInterface.sendSignUpStatusEvent(event = SignUpStatusEvent.LOADING)
                }
                is ModelState.Success -> {
                    signUpViewModelContentInterface.sendSignUpStatusEvent(event = SignUpStatusEvent.INVISIBLE)
                    it.data?.getContentIfNotHandled()?.let { isDuplicated ->
                        if (isDuplicated) {
                            signUpEnterIdViewModel.setStatusDuplicated()
                        } else {
                            signUpViewModelContentInterface.id =
                                signUpEnterIdViewModel.signUpEnterIdModel.value.inputString
                            findNavController().navigate(R.id.action_fragmentSignUpEnterId_to_fragmentSignUpEnterNickname)
                        }
                    }
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
            if (it.peekContent() == SignUpPage.ENTER_ID) {
                it.getContentIfNotHandled()?.run {
                    signUpViewModelContentInterface.id = ""
                    binding.etInput.setText("")
                }
            }
        }
    }

    override fun onDestroyView() {
        signUpViewModelContentInterface.id =
            signUpEnterIdViewModel.signUpEnterIdModel.value.inputString
        super.onDestroyView()
    }
}
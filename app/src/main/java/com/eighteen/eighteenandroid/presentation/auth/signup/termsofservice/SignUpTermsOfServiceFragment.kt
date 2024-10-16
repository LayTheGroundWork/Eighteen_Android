package com.eighteen.eighteenandroid.presentation.auth.signup.termsofservice

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpTermsOfServiceBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpPage
import com.eighteen.eighteenandroid.presentation.common.WebViewUrl
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle

/**
 * 약관 동의 페이지
 */
//TODO 선택 동의 어떻게 처리하는지?
class SignUpTermsOfServiceFragment : BaseSignUpContentFragment<FragmentSignUpTermsOfServiceBinding>(
    FragmentSignUpTermsOfServiceBinding::inflate
) {
    override val onMoveNextPageAction: () -> Unit = {
        findNavController().navigate(R.id.action_fragmentSignUpTermsOfService_to_fragmentSignUpEnterId)
    }
    override val progress: Int? = null
    override val signUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = false,
        size = SignUpNextButtonModel.Size.FULL,
        buttonText = SignUpNextButtonModel.ButtonText.NEXT
    )

    private val signUpTermsOfServiceViewModel by viewModels<SignUpTermsOfServiceViewModel>()

    override fun initView() {
        bind {
            vBtnCheckAll.setOnClickListener { signUpTermsOfServiceViewModel.checkAll() }
            vBtnCheckTermsOfService.setOnClickListener { signUpTermsOfServiceViewModel.checkTermsOfService() }
            vBtnCheckPrivacyPolicy.setOnClickListener { signUpTermsOfServiceViewModel.checkPrivacyPolicy() }
            vBtnCheckNotification.setOnClickListener { signUpTermsOfServiceViewModel.checkNotification() }
            tvBtnTermsOfServiceDetail.setOnClickListener {
                signUpViewModelContentInterface.actionOpenWebViewFragment(url = WebViewUrl.TERMS_OF_SERVICE)
            }
            tvBtnPrivacyPolicyDetail.setOnClickListener {
                signUpViewModelContentInterface.actionOpenWebViewFragment(url = WebViewUrl.PRIVACY_POLICY)
            }
        }
        initStateFlow()
    }

    private fun initStateFlow() {
        collectInLifecycle(signUpTermsOfServiceViewModel.termsOfServiceModelStateFlow) {
            bind {
                vBtnCheckAll.isSelected = it.isCheckedAll
                vBtnCheckTermsOfService.isSelected = it.isCheckedTermsOfService
                vBtnCheckPrivacyPolicy.isSelected = it.isCheckedPrivacyPolicy
                vBtnCheckNotification.isSelected = it.isCheckedNotification
            }
            signUpViewModelContentInterface.setNextButtonModel(
                signUpNextButtonModel = signUpNextButtonModel.copy(
                    isEnabled = it.isCheckedRequiredAll
                )
            )
        }

        collectInLifecycle(signUpViewModelContentInterface.pageClearEvent) {
            if (it.peekContent() == SignUpPage.TERMS_OF_SERVICE) {
                it.getContentIfNotHandled()?.run {
                    signUpTermsOfServiceViewModel.clear()
                }
            }
        }
    }
}
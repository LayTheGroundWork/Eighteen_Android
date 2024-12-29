package com.eighteen.eighteenandroid.presentation.auth.signup.enterbirth

import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpEnterBirthBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpPage
import com.eighteen.eighteenandroid.presentation.common.WebViewUrl
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.datepicker.DatePickerDialogFragment
import java.util.Calendar

class SignUpEnterBirthFragment :
    BaseSignUpContentFragment<FragmentSignUpEnterBirthBinding>(FragmentSignUpEnterBirthBinding::inflate) {

    override val onMovePrevPageAction: () -> Unit = {
        signUpViewModelContentInterface.setPageClearEvent(SignUpPage.ENTER_NICK_NAME)
        super.onMovePrevPageAction.invoke()
    }
    override val onMoveNextPageAction: () -> Unit = {
        findNavController().navigate(R.id.action_fragmentSignUpEnterBirth_to_fragmentSignUpEnterSchool)
    }
    override val progress: Int = 60
    override val signUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = false,
        size = SignUpNextButtonModel.Size.NORMAL,
        buttonText = SignUpNextButtonModel.ButtonText.NEXT
    )

    override fun initView() {
        setCalendar(calendar = signUpViewModelContentInterface.birth)
        bind {
            tvBtnSelectBirth.setOnClickListener {
                showDialogFragment(
                    DatePickerDialogFragment.newInstance(
                        requestKey = DATE_PICKER_REQUEST_KEY,
                        title = getString(R.string.sign_up_enter_birth_select_birth)
                    )
                )
            }
            tvBtnSeeTermsOfService.setOnClickListener {
                signUpViewModelContentInterface.actionOpenWebViewFragment(url = WebViewUrl.TERMS_OF_SERVICE)
            }
        }
        initFragmentResultListener()
        initStateFlow()
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            DATE_PICKER_REQUEST_KEY,
            viewLifecycleOwner,
            object : DatePickerDialogFragment.DatePickerResultListener() {
                override fun onConfirmResult(year: Int, month: Int, day: Int) {
                    val calendar = Calendar.getInstance().apply { set(year, month, day) }
                    setCalendar(calendar = calendar)
                    signUpViewModelContentInterface.birth = calendar
                }
            })
    }

    private fun setCalendar(calendar: Calendar?) {
        binding.tvBtnSelectBirth.text = calendar?.let {
            "${it.get(Calendar.YEAR)}년 ${it.get(Calendar.MONTH) + 1}월 ${it.get(Calendar.DATE)}일"
        } ?: getString(R.string.sign_up_enter_birth_select_birth)
        signUpViewModelContentInterface.setNextButtonModel(
            signUpNextButtonModel = signUpNextButtonModel.copy(
                isEnabled = calendar != null
            )
        )
    }

    private fun initStateFlow() {
        collectInLifecycle(flow = signUpViewModelContentInterface.pageClearEvent) {
            if (it.peekContent() == SignUpPage.ENTER_BIRTH) {
                it.getContentIfNotHandled()?.run {
                    setCalendar(null)
                    signUpViewModelContentInterface.birth = null
                }
            }
        }
    }

    companion object {
        private const val DATE_PICKER_REQUEST_KEY = "date_picker_request_key"
    }
}
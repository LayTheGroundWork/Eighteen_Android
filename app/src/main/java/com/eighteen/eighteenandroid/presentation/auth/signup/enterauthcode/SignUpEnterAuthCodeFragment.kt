package com.eighteen.eighteenandroid.presentation.auth.signup.enterauthcode

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpEnterAuthCodeBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.LoginType
import com.eighteen.eighteenandroid.presentation.auth.signup.enterauthcode.model.ConfirmResultModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpPage
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.clearFocus
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.hideKeyboardAndRemoveCurrentFocus
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.showKeyboard
import com.eighteen.eighteenandroid.presentation.dialog.PopUpDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.TwoButtonPopUpDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * 인증 코드 입력 페이지
 */
@AndroidEntryPoint
class SignUpEnterAuthCodeFragment :
    BaseSignUpContentFragment<FragmentSignUpEnterAuthCodeBinding>(FragmentSignUpEnterAuthCodeBinding::inflate) {
    override val onMovePrevPageAction: () -> Unit = {
        signUpViewModelContentInterface.setPageClearEvent(page = SignUpPage.ENTER_PHONE_NUMBER)
        super.onMovePrevPageAction.invoke()
    }
    override val onMoveNextPageAction: () -> Unit = {
        signUpEnterAuthCodeViewModel.requestConfirmMessage(
            phoneNumber = signUpViewModelContentInterface.phoneNumber,
            verificationCode = getCurrentAuthCode()
        )
    }
    override val progress: Int? = null

    override val signUpNextButtonModel: SignUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = false,
        size = SignUpNextButtonModel.Size.NORMAL,
        buttonText = SignUpNextButtonModel.ButtonText.NEXT
    )

    private val signUpEnterAuthCodeViewModel by viewModels<SignUpEnterAuthCodeViewModel>()

    private var isEnabledTextChangeListener = true
    private var isNeedClearFocus = false

    override fun initView() {
        initInputLayout()
        isNeedClearFocus = true
        bind {
            clInputAuthCode.children.forEach {
                it.clearFocus()
            }
            tvBtnResend.setOnClickListener {
                signUpEnterAuthCodeViewModel.requestSendMessage(phoneNumber = signUpViewModelContentInterface.phoneNumber)
            }
        }
        initStateFlow()
        collectRemainTimeFlow()
        initFragmentResultListener()
    }

    override fun onResume() {
        super.onResume()
        if (isNeedClearFocus) {
            binding.clInputAuthCode.children.forEach {
                it.clearFocus()
            }
        }
        isNeedClearFocus = false
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initInputLayout() {
        bind {
            root.setOnClickListener {
                hideKeyboardAndRemoveCurrentFocus()
            }
            val editTexts = clInputAuthCode.children.toList()
            editTexts.forEachIndexed { idx, it ->
                val editText = it as? EditText
                editText?.let { et ->
                    et.setOnEditorActionListener { _, actionId, event ->
                        if ((event?.keyCode == KeyEvent.KEYCODE_ENTER) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            clearFocus()
                        }
                        false
                    }
                    et.addTextChangedListener(
                        object : TextWatcher {
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                            }

                            override fun onTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                            }

                            override fun afterTextChanged(p0: Editable?) {
                                if (!isEnabledTextChangeListener) return
                                updateEditTextView()
                            }

                        })
                    et.setOnTouchListener { _, motionEvent ->
                        when (motionEvent.action) {
                            ACTION_DOWN -> {
                                updateEditTextView()
                            }
                        }
                        true
                    }
                }
            }
        }
    }

    private fun updateEditTextView() {
        val editTexts = binding.clInputAuthCode.children.map { it as? EditText }.toList()
        val currentText = getCurrentAuthCode()
        val focusedIndex =
            (currentText.length - 1).coerceAtLeast(0).coerceAtMost(editTexts.lastIndex)
        val focusTarget = editTexts.getOrNull(focusedIndex)
        isEnabledTextChangeListener = false
        editTexts.forEachIndexed { index, editText ->
            editText?.let { et ->
                et.isSelected = currentText.length > index
                et.setText(currentText.getOrNull(index)?.run { toString() } ?: "")
            }
        }
        focusTarget?.let {
            it.setSelection(it.text.length)
            it.requestFocus()
            showKeyboard(it)
        }
        signUpViewModelContentInterface.setNextButtonModel(
            signUpNextButtonModel = signUpNextButtonModel.copy(
                isEnabled = currentText.length >= editTexts.size
            )
        )
        isEnabledTextChangeListener = true
    }

    private fun getCurrentAuthCode() =
        binding.clInputAuthCode.children.joinToString(separator = "") {
            (it as? EditText)?.text?.toString() ?: ""
        }

    private fun initStateFlow() {
        collectInLifecycle(signUpEnterAuthCodeViewModel.sendMessageResultStateFlow) {
            when (it) {
                is ModelState.Loading -> {
                    //TODO 로딩 처리
                    Log.d("SignUpEnterAuthCodeFragment", "send loading")
                }
                is ModelState.Success -> {
                    //do nothing
                }
                is ModelState.Error -> {
                    //TODO 에러처리
                    Log.d("SignUpEnterAuthCodeFragment", "send error ${it.throwable}")

                }
                is ModelState.Empty -> {
                    //do nothing
                }
            }
        }

        collectInLifecycle(signUpEnterAuthCodeViewModel.confirmMessageResultStateFlow) {
            when (it) {
                is ModelState.Loading -> {
                    //TODO 로딩 처리
                    Log.d("SignUpEnterAuthCodeFragment", "confirm loading")
                }
                is ModelState.Success -> {
                    if (loginType == LoginType.SIGNUP) {
                        val data = it.data
                        if (data is ConfirmResultModel.LoginSuccess) showLoginDialogFragment()
                        else showSignUpDialogFragment()
                    } else if (loginType == LoginType.LOGIN) {
                        val data = it.data
                        if (data is ConfirmResultModel.LoginSuccess) {
                            signUpViewModelContentInterface.requestLogin(authToken = data.authToken)
                        } else {
                            //TODO 로그인 실패
                        }
                    }
                }
                is ModelState.Error -> {
                    //TODO 에러처리
                    Log.d("SignUpEnterAuthCodeFragment", "confirm error ${it.throwable}")
                }
                is ModelState.Empty -> {
                    //do nothing
                }
            }
        }
    }

    private fun showSignUpDialogFragment() {
        val title = getString(R.string.sign_up_enter_auth_code_sign_up_dialog_title)
        val content = getString(R.string.sign_up_enter_auth_code_sign_up_dialog_content)
        val buttonText = getString(R.string.ok_like)
        val dialogFragment =
            PopUpDialogFragment.newInstance(
                requestKey = REQUEST_KEY_SIGN_UP_DIALOG,
                title = title,
                content = content,
                buttonText = buttonText
            )
        showDialogFragment(dialogFragment = dialogFragment)
    }

    private fun showLoginDialogFragment() {
        val title = getString(R.string.sign_up_enter_auth_code_login_dialog_title)
        val content = getString(R.string.sign_up_enter_auth_code_login_dialog_content)
        val confirmButtonText = getString(R.string.ok_like)
        val rejectButtonText = getString(R.string.no)

        val dialogFragment = TwoButtonPopUpDialogFragment.newInstance(
            requestKey = REQUEST_KEY_LOGIN_DIALOG,
            title = title,
            content = content,
            confirmButtonText = confirmButtonText,
            rejectButtonText = rejectButtonText
        )
        showDialogFragment(dialogFragment = dialogFragment)
    }

    private fun collectRemainTimeFlow() {
        collectInLifecycle(signUpEnterAuthCodeViewModel.remainTimeFlow) {
            binding.tvRemainTime.text = it
        }
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY_SIGN_UP_DIALOG,
            viewLifecycleOwner,
            object : PopUpDialogFragment.PopUpDialogFragmentResultListener() {
                override fun onConfirm() {
                    findNavController().navigate(R.id.action_fragmentSignUpEnterAuthCode_to_fragmentSignUpTermsOfService)
                }
            })

        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY_LOGIN_DIALOG,
            viewLifecycleOwner,
            object : TwoButtonPopUpDialogFragment.TowButtonPopUpDialogFragmentResultListener() {
                override fun onConfirm() {
                    (signUpEnterAuthCodeViewModel.confirmMessageResultStateFlow.value.data as? ConfirmResultModel.LoginSuccess)?.let {
                        signUpViewModelContentInterface.requestLogin(it.authToken)
                    }
                }
            })
    }

    override fun onDestroyView() {
        signUpEnterAuthCodeViewModel.clear()
        super.onDestroyView()
    }

    companion object {
        private const val REQUEST_KEY_SIGN_UP_DIALOG = "REQUEST_KEY_SIGN_UP_DIALOG"
        private const val REQUEST_KEY_LOGIN_DIALOG = "REQUEST_KEY_LOGIN_DIALOG"
    }
}
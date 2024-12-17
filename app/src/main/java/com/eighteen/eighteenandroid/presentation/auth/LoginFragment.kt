package com.eighteen.eighteenandroid.presentation.auth

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentLoginBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.LoginType
import com.eighteen.eighteenandroid.presentation.auth.signup.SignUpFragment.Companion.ARGUMENT_LOGIN_TYPE_KEY

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    override fun initView() {
        bind {
            tvNoticeAgreed.text = createNoticeAgreedSpannable()
            tvBtnSignUpWithPhoneNumber.setOnClickListener {
                findNavController().navigate(
                    R.id.action_fragmentLogin_to_fragmentSignUp,
                    bundleOf(ARGUMENT_LOGIN_TYPE_KEY to LoginType.SIGNUP)
                )
            }
            tvBtnLogin.setOnClickListener {
                findNavController().navigate(
                    R.id.action_fragmentLogin_to_fragmentSignUp,
                    bundleOf(ARGUMENT_LOGIN_TYPE_KEY to LoginType.LOGIN)
                )
            }
            ivBtnClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun createNoticeAgreedSpannable(): Spannable {
        val noticeAgreedString = resources.getString(R.string.sign_up_login_notice_agreed)
        return SpannableStringBuilder(noticeAgreedString).apply {
            val context = context ?: return@apply
            setSpan(
                TextAppearanceSpan(context, R.style.pretendard_regular_12),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_02)),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val blackColorStrings =
                listOf(R.string.korea, R.string.terms_of_service, R.string.privacy_policy).map {
                    resources.getString(it)
                }
            blackColorStrings.forEach {
                val firstIndex = indexOf(it)
                val lastIndex = firstIndex + it.length
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)),
                    firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }.toSpannable()
    }
}
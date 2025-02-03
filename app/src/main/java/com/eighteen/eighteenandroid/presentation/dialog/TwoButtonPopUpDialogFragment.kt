package com.eighteen.eighteenandroid.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.eighteen.eighteenandroid.databinding.FragmentTwoButtonPopUpDialogBinding
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment

class TwoButtonPopUpDialogFragment :
    BaseDialogFragment<FragmentTwoButtonPopUpDialogBinding>(FragmentTwoButtonPopUpDialogBinding::inflate) {
    override fun initView() {
        arguments?.let {
            bind {
                arguments?.getInt(ARGUMENT_DRAWABLE_RES_KEY).takeIf { it != -1 }?.let {
                    ivIllustration.isVisible = true
                    ivIllustration.setImageResource(it)
                } ?: run {
                    ivIllustration.isVisible = false
                }
                val title = it.getString(ARGUMENT_TITLE_KEY)
                tvTitle.text = title
                tvTitle.isVisible = title != null
                val content = it.getString(ARGUMENT_CONTENT_KEY)
                tvContent.text = content
                tvContent.isVisible = content != null
                val confirmButtonText = it.getString(ARGUMENT_CONFIRM_BUTTON_TEXT)
                tvBtnConfirm.text = confirmButtonText
                val rejectButtonText = it.getString(ARGUMENT_REJECT_BUTTON_TEXT)
                tvBtnReject.text = rejectButtonText
                cvBtnConfirm.setOnClickListener {
                    arguments?.getString(ARGUMENT_REQUEST_KEY_KEY)?.let {
                        val result =
                            bundleOf(RESULT_CONFIRM_KEY to true)
                        setFragmentResult(requestKey = it, result = result)
                        dismissNow()
                    }
                }
                cvBtnReject.setOnClickListener {
                    arguments?.getString(ARGUMENT_REQUEST_KEY_KEY)?.let {
                        val result = bundleOf(RESULT_REJECT_KEY to true)
                        setFragmentResult(requestKey = it, result = result)
                        dismissNow()
                    }
                }
                context?.let { context ->
                    it.getInt(ARGUMENT_REJECT_BUTTON_COLOR, -1).takeIf { it != -1 }
                        ?.let { colorRes ->
                            cvBtnReject.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    colorRes
                                )
                            )
                        }
                    it.getInt(ARGUMENT_CONFIRM_BUTTON_COLOR, -1).takeIf { it != -1 }
                        ?.let { colorRes ->
                            cvBtnConfirm.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    colorRes
                                )
                            )
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initDialogWindow()
    }

    private fun initDialogWindow() {
        dialog?.run {
            window?.run {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    companion object {
        private const val ARGUMENT_REQUEST_KEY_KEY = "ARGUMENT_REQUEST_KEY_KEY"
        private const val ARGUMENT_DRAWABLE_RES_KEY = "ARGUMENT_DRAWABLE_RES_KEY"
        private const val ARGUMENT_TITLE_KEY = "ARGUMENT_TITLE_KEY"
        private const val ARGUMENT_CONTENT_KEY = "ARGUMENT_CONTENT_KEY"
        private const val ARGUMENT_CONFIRM_BUTTON_TEXT = "ARGUMENT_CONFIRM_BUTTON_TEXT"
        private const val ARGUMENT_REJECT_BUTTON_TEXT = "ARGUMENT_REJECT_BUTTON_TEXT"
        private const val ARGUMENT_CONFIRM_BUTTON_COLOR = "ARGUMENT_CONFIRM_BUTTON_COLOR"
        private const val ARGUMENT_REJECT_BUTTON_COLOR = "ARGUMENT_REJECT_BUTTON_COLOR"

        private const val RESULT_CONFIRM_KEY = "RESULT_CONFIRM_KEY"
        private const val RESULT_REJECT_KEY = "RESULT_REJECT_KEY"
        fun newInstance(
            requestKey: String? = null,
            @DrawableRes drawableRes: Int = -1,
            title: String? = null,
            content: String? = null,
            confirmButtonText: String? = null,
            rejectButtonText: String? = null,
            @ColorRes confirmButtonColorRes: Int? = null,
            @ColorRes rejectButtonColorRes: Int? = null
        ) = TwoButtonPopUpDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_REQUEST_KEY_KEY, requestKey)
                putInt(ARGUMENT_DRAWABLE_RES_KEY, drawableRes)
                putString(ARGUMENT_TITLE_KEY, title)
                putString(ARGUMENT_CONTENT_KEY, content)
                putString(ARGUMENT_CONFIRM_BUTTON_TEXT, confirmButtonText)
                putString(ARGUMENT_REJECT_BUTTON_TEXT, rejectButtonText)
                confirmButtonColorRes?.let {
                    putInt(ARGUMENT_CONFIRM_BUTTON_COLOR, it)
                }
                rejectButtonColorRes?.let {
                    putInt(ARGUMENT_REJECT_BUTTON_COLOR, it)
                }
            }
        }
    }

    abstract class TowButtonPopUpDialogFragmentResultListener : FragmentResultListener {
        final override fun onFragmentResult(requestKey: String, result: Bundle) {
            result.getBoolean(RESULT_CONFIRM_KEY, false).takeIf { it }?.let {
                onConfirm()
            }
            result.getBoolean(RESULT_REJECT_KEY, false).takeIf { it }?.let {
                onReject()
            }
        }

        open fun onConfirm() {}
        open fun onReject() {}
    }
}
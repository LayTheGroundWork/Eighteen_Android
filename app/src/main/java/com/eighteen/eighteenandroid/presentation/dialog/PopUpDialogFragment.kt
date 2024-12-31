package com.eighteen.eighteenandroid.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import com.eighteen.eighteenandroid.databinding.FragmentPopUpDialogBinding
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment

class PopUpDialogFragment :
    BaseDialogFragment<FragmentPopUpDialogBinding>(FragmentPopUpDialogBinding::inflate) {
    override fun initView() {
        arguments?.let {
            bind {
                val title = it.getString(ARGUMENT_TITLE_KEY)
                tvTitle.text = title
                tvTitle.isVisible = title != null
                val content = it.getString(ARGUMENT_CONTENT_KEY)
                tvContent.text = content
                tvContent.isVisible = content != null
                val buttonText = it.getString(ARGUMENT_BUTTON_TEXT)
                tvBtnConfirm.text = buttonText
                val buttonColorRes = it.getInt(ARGUMENT_BUTTON_COLOR_RES_KEY, -1)
                if (buttonColorRes != -1) {
                    context?.let {
                        tvBtnConfirm.setBackgroundColor(ContextCompat.getColor(it, buttonColorRes))
                    }
                }
                tvBtnConfirm.setOnClickListener {
                    arguments?.getString(ARGUMENT_REQUEST_KEY_KEY)?.let {
                        val result =
                            bundleOf(RESULT_CONFIRM_KEY to true)
                        setFragmentResult(requestKey = it, result = result)
                    }
                    dismissNow()
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
        private const val ARGUMENT_TITLE_KEY = "ARGUMENT_TITLE_KEY"
        private const val ARGUMENT_CONTENT_KEY = "ARGUMENT_CONTENT_KEY"
        private const val ARGUMENT_BUTTON_COLOR_RES_KEY = "ARGUMENT_BUTTON_COLOR_RES_KEY"
        private const val ARGUMENT_BUTTON_TEXT = "ARGUMENT_BUTTON_TEXT"

        private const val RESULT_CONFIRM_KEY = "RESULT_CONFIRM_KEY"
        fun newInstance(
            requestKey: String? = null,
            title: String? = null,
            content: String? = null,
            @ColorRes buttonColorRes: Int = -1,
            buttonText: String? = null
        ) = PopUpDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_REQUEST_KEY_KEY, requestKey)
                putString(ARGUMENT_TITLE_KEY, title)
                putString(ARGUMENT_CONTENT_KEY, content)
                putInt(ARGUMENT_BUTTON_COLOR_RES_KEY, buttonColorRes)
                putString(ARGUMENT_BUTTON_TEXT, buttonText)
            }
        }
    }

    abstract class PopUpDialogFragmentResultListener : FragmentResultListener {
        final override fun onFragmentResult(requestKey: String, result: Bundle) {
            result.getBoolean(RESULT_CONFIRM_KEY, false).takeIf { it }?.let {
                onConfirm()
            }
        }

        open fun onConfirm() {}
    }
}
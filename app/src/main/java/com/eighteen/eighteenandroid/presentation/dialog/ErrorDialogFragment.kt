package com.eighteen.eighteenandroid.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import com.eighteen.eighteenandroid.databinding.LayoutErrorDialogBinding
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment

class ErrorDialogFragment :
    BaseDialogFragment<LayoutErrorDialogBinding>(LayoutErrorDialogBinding::inflate) {
    override fun initView() {
        bind {
            tvBtnConfirm.setOnClickListener {
                dismissNow()
            }
        }
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
}
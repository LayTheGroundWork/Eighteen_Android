package com.eighteen.eighteenandroid.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.DialogReportCompletedBinding
import com.eighteen.eighteenandroid.databinding.DialogReportContentBinding
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment

/**
 *
 * @file ReportDialogFragment.kt
 * @date 06/03/2024
 * 신고사유 입력 다이얼로그
 */
class ReportDialogFragment :
    BaseDialogFragment<DialogReportContentBinding>(DialogReportContentBinding::inflate) {

    override fun initView() {
        bind {
            btnDismissDialog.setOnClickListener {
                this@ReportDialogFragment.dismiss()
            }
            btnReport.setOnClickListener {
                // TODO. User 신고처리

                // 처리 후에 결과 Dialog 보여주기
                val binding =
                    DialogReportCompletedBinding.inflate(LayoutInflater.from(requireContext()))
                val reportCompletedDialog =
                    AlertDialog.Builder(requireContext(), R.style.CustomDialog)
                        .setView(binding.root)
                        .create()

                binding.btnReportCompleted.setOnClickListener {
                    reportCompletedDialog.dismiss()
                }

                reportCompletedDialog.show()
                this@ReportDialogFragment.dismiss()
            }
        }
    }

    companion object {
        const val TAG = "ReportDialogFragment"
        const val KEY_USER_ID = "USER_ID"
        const val KEY_USER_NAME = "USER_NAME"

        fun newInstance(user: User): ReportDialogFragment {
            val bundle = Bundle().apply {
                putString(KEY_USER_ID, user.uniqueId)
                putString(KEY_USER_NAME, user.userName)
            }
            return ReportDialogFragment().apply { arguments = bundle }
        }

        fun newInstance(userId: String, userName: String) = ReportDialogFragment().apply {
            arguments = bundleOf(KEY_USER_ID to userId, KEY_USER_NAME to userName)
        }
    }
}
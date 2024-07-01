package com.eighteen.eighteenandroid.presentation.common

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.DialogReportSelectBinding

fun Fragment.showDialogFragment(dialogFragment: DialogFragment, tag: String? = null) {
    dialogFragment.show(childFragmentManager, tag)
}

// 신고하기 & 차단하기 다이얼로그
fun showReportDialog(context: Context, showDialog: () -> Unit) {
    val reportSelectBinding = DialogReportSelectBinding.inflate(LayoutInflater.from(context))
    val customAlertDialog = AlertDialog.Builder(context, R.style.CustomDialog)
        .setView(reportSelectBinding.root)
        .create()
    customAlertDialog.show()
    reportSelectBinding.txReport.setOnClickListener {
        showDialog()
        customAlertDialog.dismiss()
    }
}

fun <T : Parcelable> Fragment.getNavigationResult(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T : Parcelable> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T : Parcelable> Fragment.setNavigationResult(
    key: String,
    result: T,
    @IdRes destinationId: Int
) {
    findNavController().getBackStackEntry(destinationId).savedStateHandle[key] = result
}
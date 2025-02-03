package com.eighteen.eighteenandroid.presentation.myprofile.setting.managinginfo

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentManagingInfoBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.ErrorDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.TwoButtonPopUpDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagingInfoFragment :
    BaseFragment<FragmentManagingInfoBinding>(FragmentManagingInfoBinding::inflate) {
    private val managingInfoViewModel by viewModels<ManagingInfoViewModel>()
    private val myViewModel by activityViewModels<MyViewModel>()

    override fun initView() {
        bind {
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            ivBtnDeleteUser.setOnClickListener {
                showDialogFragment(
                    TwoButtonPopUpDialogFragment.newInstance(
                        requestKey = DELETE_USER_DIALOG_REQUEST_KEY,
                        title = getString(R.string.my_profile_setting_managing_info_delete_user_dialog_title),
                        content = getString(R.string.my_profile_setting_managing_info_delete_user_dialog_content),
                        confirmButtonText = getString(R.string.confirm),
                        rejectButtonText = getString(R.string.my_profile_setting_managing_info_dialog_reject),
                        confirmButtonColorRes = R.color.red
                    )
                )
            }
            ivBtnSignOut.setOnClickListener {
                showDialogFragment(
                    TwoButtonPopUpDialogFragment.newInstance(
                        requestKey = SIGN_OUT_DIALOG_REQUEST_KEY,
                        title = getString(R.string.my_profile_setting_managing_info_sign_out_dialog_title),
                        confirmButtonText = getString(R.string.confirm),
                        rejectButtonText = getString(R.string.my_profile_setting_managing_info_dialog_reject),
                        confirmButtonColorRes = R.color.red
                    )
                )
            }
        }
        initEvent()
        initFragmentResultListener()
    }

    private fun initEvent() {
        collectInLifecycle(managingInfoViewModel.deleteUserEventChannel) {
            binding.inLoading.root.isVisible = it is ModelState.Loading
            when (it) {
                is ModelState.Success -> {
                    myViewModel.completeLogout()
                    findNavController().popBackStack(R.id.fragmentMain, false)

                }
                is ModelState.Error -> {
                    showDialogFragment(ErrorDialogFragment())
                }
                else -> {
                    //do nothing
                }
            }
        }
        collectInLifecycle(managingInfoViewModel.signOutEventChannel) {
            binding.inLoading.root.isVisible = it is ModelState.Loading
            when (it) {
                is ModelState.Success -> {
                    myViewModel.completeLogout()
                    findNavController().popBackStack(R.id.fragmentMain, false)
                }
                is ModelState.Error -> {
                    showDialogFragment(ErrorDialogFragment())
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(SIGN_OUT_DIALOG_REQUEST_KEY,
            viewLifecycleOwner,
            object : TwoButtonPopUpDialogFragment.TowButtonPopUpDialogFragmentResultListener() {
                override fun onConfirm() {
                    managingInfoViewModel.signOut()
                }
            })
        childFragmentManager.setFragmentResultListener(DELETE_USER_DIALOG_REQUEST_KEY,
            viewLifecycleOwner,
            object : TwoButtonPopUpDialogFragment.TowButtonPopUpDialogFragmentResultListener() {
                override fun onConfirm() {
                    managingInfoViewModel.deleteUser()
                }
            })
    }

    companion object {
        private const val SIGN_OUT_DIALOG_REQUEST_KEY = "sign_out_dialog_request_key"
        private const val DELETE_USER_DIALOG_REQUEST_KEY = "delete_user_dialog_request_key"
    }
}
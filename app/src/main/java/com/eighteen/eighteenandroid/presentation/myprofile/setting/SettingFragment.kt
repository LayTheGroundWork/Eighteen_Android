package com.eighteen.eighteenandroid.presentation.myprofile.setting

import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSettingBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment

//TODO 버튼 클릭 기능 추가
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    override fun initView() {
        bind {
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            ivBtnManagingInfo.setOnClickListener {
                findNavController().navigate(R.id.fragmentManagingInfo)
            }
        }
    }
}
package com.eighteen.eighteenandroid.presentation.myprofile

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentMyProfileBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.myprofile.editlink.EditLinkDialogFragment
import kotlinx.coroutines.launch

class MyProfileFragment :
    BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate),
    MyProfileClickListener {

    private val myViewModel by activityViewModels<MyViewModel>()
    private val myProfileViewModel by viewModels<MyProfileViewModel>()

    override fun initView() {
        initStateFlow()
        binding.rvProfile.run {
            adapter = MyProfileAdapter(clickListener = this@MyProfileFragment)
            itemAnimator = null
            addItemDecoration(MyProfileItemDecoration())
        }
    }

    private fun initStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myViewModel.myProfileStateFlow.collect {
                    if (it.isSuccess()) {
                        it.data?.let { profile ->
                            myProfileViewModel.setProfile(profile)
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myProfileViewModel.myProfileItemsStateFlow.collect {
                    (binding.rvProfile.adapter as? MyProfileAdapter)?.submitList(it)
                }
            }
        }
    }

    override fun onClickSetting() {
        //TODO 설정화면 이동
        Log.d("MyProfileFragment", "onClickSetting")
    }

    override fun onClickEditMedia() {
        findNavController().navigate(R.id.fragmentMyEditMedia)
    }

    override fun onClickEditNickName() {
        findNavController().navigate(R.id.fragmentEditNickname)
    }

    override fun onClickEditSchool() {
        findNavController().navigate(R.id.fragmentEditSchool)
    }

    override fun onClickBadge() {
        findNavController().navigate(R.id.fragmentBadgeDetail)
    }

    override fun onClickTeen() {
        //TODO Teen 눌렀을 때
        Log.d("MyProfileFragment", "onClickTeen")
    }

    override fun onClickEditLink() {
        val editLinkDialogFragment = EditLinkDialogFragment()
        showDialogFragment(dialogFragment = editLinkDialogFragment)
    }

    //TODO 링크 직접 클릭 시 외부링크 연결?

    override fun onClickEditIntroduce() {
        findNavController().navigate(R.id.action_fragmentMyProfile_to_fragmentEditIntroduce)
    }

    override fun onClickEditTenOfQna() {
        findNavController().navigate(R.id.action_fragmentMyPRofile_to_fragmentEditTenOfQna)
    }

    override fun onClickExpandQna() {
        myProfileViewModel.toggleQnaExpanded()
    }
}
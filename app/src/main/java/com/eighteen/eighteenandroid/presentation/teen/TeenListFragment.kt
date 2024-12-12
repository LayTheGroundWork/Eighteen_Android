package com.eighteen.eighteenandroid.presentation.teen

import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentTeenListBinding
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.showReportSelectDialogLeft
import com.eighteen.eighteenandroid.presentation.dialog.ReportDialogFragment
import com.eighteen.eighteenandroid.presentation.teen.adapter.TeenListAdapter
import com.eighteen.eighteenandroid.presentation.teen.adapter.TeenListAdapterListener

class TeenListFragment: BaseFragment<FragmentTeenListBinding>(FragmentTeenListBinding::inflate) {

    override fun initView() {
        bind {
            val title = arguments?.getString("title")
            title?.let {
                tvTeenListTitle.text = it.replace("\n", " ")
            }

            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            // adapter 연결
            val adapter = TeenListAdapter(object: TeenListAdapterListener {
                override fun onUserClicks(user: User) {
                    findNavController().navigate(R.id.action_fragmentTeenList_to_fragmentProfileDetail)   // 유저 상세로 이동
                }

                override fun onUserLikeClicks(user: User) {
                    // TODO. 좋아요 API 연결
                }

                override fun onUserChatClicks(user: User) {
                    // 채팅 화면 이동
                }

                override fun onUserMoreClicks(itemView: View, user: User) {
                    showReportSelectDialogLeft(
                        itemView,
                        onReportClicked = {
                            showDialogFragment(ReportDialogFragment.newInstance(user))
                        },
                        onBlockClicked = {}
                    )
                }
            })

            rvTeenList.layoutManager = LinearLayoutManager(requireContext())
            rvTeenList.adapter = adapter

            // TODO. Title에 해당하는 Teen List 가져오기
            adapter.submitList(
                listOf(
                    User(
                        userImage = "https://image.blip.kr/v1/file/021ec61ff1c9936943383b84236a0e69",
                        userId = "1",
                        userName = "김 에스더",
                        userAge = "16",
                        userSchoolName = "서울 중학교",
                    ),
                    User(
                        userImage = "https://cdn.newsculture.press/news/photo/202308/529742_657577_5726.jpg",
                        userId = "2",
                        userName = "김 에스더",
                        userAge = "16",
                        userSchoolName = "부천 중학교",
                    ),
                    User(
                        userImage = "https://mblogthumb-phinf.pstatic.net/MjAyMTEwMzFfMTY1/MDAxNjM1NjUzMTI2NjI3.xXYQteLLoWLKcR9YnXS0Hk_y-DInauMzF25g7FxlcScg.2Y-neBBMVoP2IhcwzX2Zy2HB2d8EnM_cY76FVLuk_1Yg.JPEG.ssun2415/IMG_4148.jpg?type=w800",
                        userId = "3",
                        userName = "김 에스더",
                        userAge = "16",
                        userSchoolName = "인천 중학교",
                    )
                )
            )
        }
    }
}
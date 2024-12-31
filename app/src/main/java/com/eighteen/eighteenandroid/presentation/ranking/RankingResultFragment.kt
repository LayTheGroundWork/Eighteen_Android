package com.eighteen.eighteenandroid.presentation.ranking

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import com.eighteen.eighteenandroid.databinding.FragmentRankingResultBinding
import com.eighteen.eighteenandroid.domain.model.UserRankInfo
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RankingResultFragment : BaseFragment<FragmentRankingResultBinding>(FragmentRankingResultBinding::inflate) {
    companion object {
        const val ARGUMENT_KEY_TOURNAMENT_NO = "tournamentNo"
        const val ARGUMENT_KEY_CATEGORY = "category"
    }

    @Inject
    lateinit var rankingResultAssistedFactory: RankingResultViewModel.UserRankingAssistedFactory

    private lateinit var rankingResultAdapter: RankingResultAdapter

    private val viewModel: RankingResultViewModel by viewModels(factoryProducer = {
        RankingResultViewModel.Factory(
            assistedFactory = rankingResultAssistedFactory,
            tournamentNo = 48 /*arguments?.getInt(ARGUMENT_KEY_TOURNAMENT_NO) ?: 1*/ // 임시로 데이터 많은 토너먼트 번호 48로 해두었습니다.
        )
    })

    override fun initView() {
        initAdapter()

        bind {
            tvDescription.text = String.format("%s 부문 TEEN 순위", arguments?.getString(ARGUMENT_KEY_CATEGORY))    // 배너 텍스트

            with(rvRankingResult) {
                adapter = rankingResultAdapter
            }
        }

        initStateFlow()
    }

    private fun initStateFlow() {
        collectInLifecycle(viewModel.rankingListStateFlow) {
            when (it) {
                is ModelState.Loading -> {
                    //TODO 로딩 처리
                }
                is ModelState.Success -> {
                    bind {
                        it.data?.let { userRankInfoList ->
                            if (userRankInfoList.size <= 3) {
                                // 3등 이하일 경우 모두 개별 레이아웃으로 표시
                                layout1st.visibility = if (userRankInfoList.isNotEmpty()) View.VISIBLE else View.GONE
                                layout2nd.visibility = if (userRankInfoList.size >= 2) View.VISIBLE else View.GONE
                                layout3rd.visibility = if (userRankInfoList.size >= 3) View.VISIBLE else View.GONE
                                rvRankingResult.visibility = View.GONE

                                // 각 순위별 데이터 바인딩 (실제 데이터 바인딩 로직은 추가 필요)
                                if (userRankInfoList.isNotEmpty()) bindFirstUser(userRankInfoList[0])
                                if (userRankInfoList.size > 1) bindSecondUser(userRankInfoList[1])
                                if (userRankInfoList.size > 2) bindThirdUser(userRankInfoList[2])
                            } else {
                                // 4등 이상이 있는 경우
                                layout1st.visibility = View.VISIBLE
                                layout2nd.visibility = View.VISIBLE
                                layout3rd.visibility = View.VISIBLE
                                rvRankingResult.visibility = View.VISIBLE

                                // 1~3등 데이터 바인딩
                                bindFirstUser(userRankInfoList[0])
                                bindSecondUser(userRankInfoList[1])
                                bindThirdUser(userRankInfoList[2])

                                // 4등 이후 데이터는 리사이클러뷰에 표시
                                rankingResultAdapter.submitList(userRankInfoList.subList(3, userRankInfoList.size))
                            }
                        }

                    }
                }
                is ModelState.Error -> {
                    //TODO 에러처리
                }
                is ModelState.Empty -> {
                    //do nothing
                }
            }
        }
    }

    private fun initAdapter() {
        rankingResultAdapter = RankingResultAdapter()
    }

    @SuppressLint("SetTextI18n")
    private fun bindFirstUser(data: UserRankInfo) {
        bind {
            tvNumber1st.text = data.rank.toString()
            tvUserId1st.text = data.rankerId
            tvUserName1st.text = data.rankerNickName
            tvUserPercent1st.text = "${data.voteRate}%"

            // 프로필 사진
//            Glide.with(requireContext())
//                .load(data.profileImageUrl)
//                .into(ivUser1st)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindSecondUser(data: UserRankInfo) {
        bind {
            tvNumber2nd.text = data.rank.toString()
            tvUserId2nd.text = data.rankerId
            tvUserName2nd.text = data.rankerNickName
            tvUserPercent2nd.text = "${data.voteRate}%"

            // 프로필 사진
//            Glide.with(requireContext())
//                .load(data.profileImageUrl)
//                .into(ivUser2nd)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindThirdUser(data: UserRankInfo) {
        bind {
            tvNumber3rd.text = data.rank.toString()
            tvUserId3rd.text = data.rankerId
            tvUserName3rd.text = data.rankerNickName
            tvUserPercent3rd.text = "${data.voteRate}%"

            // 프로필 사진
//            Glide.with(requireContext())
//                .load(data.profileImageUrl)
//                .into(ivUser3rd)
        }
    }

    override fun onDestroyView() {
        viewModel.clear()
        super.onDestroyView()
    }
}
package com.eighteen.eighteenandroid.presentation.ranking.votingComplete

import android.graphics.Color
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentVotingCompleteBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VotingCompleteFragment :
    BaseFragment<FragmentVotingCompleteBinding>(FragmentVotingCompleteBinding::inflate) {

    private val votingCompleteViewModel by viewModels<VotingCompleteViewModel>()
    override fun initView() {
        val winnerName = arguments?.getString("winnerName")
        val category = arguments?.getString("category")
        val winnerId = arguments?.getString("winnerId")
        val imgUrl = arguments?.getString("imgUrl")
        val age = arguments?.getString("age")
        val school = arguments?.getString("school")

        bind {
            tvName.text = winnerName
            tvCategoryCompleteTitle.text = category
            tvBtnComplete.setOnClickListener {
                votingCompleteViewModel.requestSubmitTopParticipantsList(
                    tournamentNo = 84, participantIdsOrderByRank = listOf(
                        "tester22",
                        "tester23",
                        "tester24",
                        "tester25"
                    )
                )
            }
            ivClose.setColorFilter(Color.WHITE)
            tvSchool.text = "$school, "
            tvAge.text = age
        }

        ImageLoader.get().loadUrl(
            binding.ivWinner,
            imgUrl
        )
//        initStateFlow()
    }

    private fun initStateFlow() {
        collectInLifecycle(votingCompleteViewModel.topParticipants) {
            when (it) {
                is ModelState.Loading -> {
                    //TODO 로딩 처리
                }

                is ModelState.Success -> {
                    Log.d("VotingCompleteFragment", it.data.toString())
                    findNavController().popBackStack(R.id.fragmentRanking, true)
                }

                is ModelState.Error -> {
                    Log.e(
                        "VotingCompleteFragment",
                        "Error post participants",
                        it.throwable
                    )
                    findNavController().popBackStack(R.id.fragmentRanking, true)
                }

                is ModelState.Empty -> {

                }
            }
        }
    }
}
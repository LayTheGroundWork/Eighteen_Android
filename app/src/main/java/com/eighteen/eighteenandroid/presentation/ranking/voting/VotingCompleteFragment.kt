package com.eighteen.eighteenandroid.presentation.ranking.voting

import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentVotingCompleteBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment

class VotingCompleteFragment :
    BaseFragment<FragmentVotingCompleteBinding>(FragmentVotingCompleteBinding::inflate) {
    override fun initView() {
        val winnerName = arguments?.getString("winnerName")
        val category = arguments?.getString("category")
        val winnerId = arguments?.getString("winnerId")

        bind {
            tvName.text = winnerName
            tvCategoryCompleteTitle.text = category
            tvBtnComplete.setOnClickListener{
                findNavController().navigate(R.id.fragmentMain)
            }
        }
    }
}
package com.eighteen.eighteenandroid.presentation.ranking

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.eighteen.eighteenandroid.databinding.FragmentRankingVotingDialogBinding
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment

class RankingVotingDialogFragment :
    BaseDialogFragment<FragmentRankingVotingDialogBinding>(FragmentRankingVotingDialogBinding::inflate) {
    private val requestKey by lazy {
        arguments?.getString(ARGUMENT_REQUEST_KEY)
    }

    private val id by lazy {
        arguments?.getString(ARGUMENT_ID_KEY)
    }

    private val categoryTitle by lazy {
        arguments?.getString(ARGUMENT_CATEGORY_TITLE)
    }

    override fun initView() {
        bind {
            categoryTitle?.let { category ->
                binding.tvTitle.text = "$category 투표에 참여하시겠어요?"
            }

            tvBtnCancel.setOnClickListener {
                requestKey?.let {
                    setFragmentResult(
                        requestKey = it,
                        result = bundleOf(RESULT_CANCEL_KEY to id)
                    )
                }
                dismissNow()
            }
            tvBtnConfirm.setOnClickListener {
                requestKey?.let {
                    setFragmentResult(
                        requestKey = RankingFragment.REQUEST_KEY_VOTING_ROOM_ENTER,
                        result = bundleOf(
                            RESULT_CONFIRM_KEY to id,
                            ARGUMENT_CATEGORY_TITLE to categoryTitle
                        )
                    )
                }
                dismissNow()
            }
        }
    }

    override fun onResume() {
        super.onResume()
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

    companion object {
        private const val ARGUMENT_REQUEST_KEY = "ARGUMENT_REQUEST_KEY"
        private const val ARGUMENT_ID_KEY = "ARGUMENT_ID_KEY"
        const val ARGUMENT_CATEGORY_TITLE = "ARGUMENT_CATEGORY_TITLE"
        const val RESULT_CONFIRM_KEY = "RESULT_YES_KEY"
        const val RESULT_CANCEL_KEY = "RESULT_NO_KEY"

        fun newInstance(requestKey: String, id: String, categoryTitle: String) =
            RankingVotingDialogFragment().apply {
                arguments = bundleOf(
                    ARGUMENT_REQUEST_KEY to requestKey,
                    ARGUMENT_ID_KEY to id,
                    ARGUMENT_CATEGORY_TITLE to categoryTitle
                )
            }
    }
}
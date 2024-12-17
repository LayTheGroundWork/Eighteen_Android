package com.eighteen.eighteenandroid.presentation.ranking

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.databinding.FragmentRankingBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.createChip
import com.eighteen.eighteenandroid.presentation.common.setTagStyle
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem
import com.eighteen.eighteenandroid.presentation.ranking.model.RankingCategory
import com.google.android.material.chip.Chip

class RankingFragment : BaseFragment<FragmentRankingBinding>(FragmentRankingBinding::inflate) {
    private var selectedChip: Chip? = null
    private lateinit var categories: List<RankingCategory.Category>
    private var categoryTitle: String? = null
    override fun initView() {
//        val adapter = RankingAdapter()

        val adapter = RankingAdapter { voteCard ->
            val category = categories.find { category ->
                category.cardListItems.any { it.id == voteCard.id }
            }
            categoryTitle = category?.categoryTitle ?: "기본 카테고리"

            val votingDialog = RankingVotingDialogFragment.newInstance(
                requestKey = REQUEST_KEY_VOTING_ROOM_ENTER,
                id = voteCard.id,
                categoryTitle = categoryTitle?: "기본 카테고리"
            )
            showDialogFragment(votingDialog)
        }
        initChipGroup()


        binding.rvRanking.run {
            this.adapter = adapter
        }

        val categoryList = listOf("운동", "뷰티", "공부", "예술", "게임")

        categories = (0..9).map { index ->
            val titleIndex = index % categoryList.size
            RankingCategory.Category(
                id = index.toString(),
                categoryTitle = categoryList[titleIndex],
                cardListItems = listOf(
                    CardListItem.VoteCard(
                        id = "vote_$index",
                        category = "카테고리 $index",
                        illustrationUrl = "https://png.pngtree.com/thumb_back/fw800/back_our/20190625/ourmid/pngtree-beautiful-nice-sky-blue-background-image_260273.jpg"
                    ),
                    CardListItem.WinnerCard(
                        id = "winner_${index}_1",
                        category = "카테고리 $index",
                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
                        tournamentNumb = 1
                    ),
                    CardListItem.WinnerCard(
                        id = "winner_${index}_2",
                        category = "카테고리 $index",
                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
                        tournamentNumb = 2
                    ),
                    CardListItem.WinnerCard(
                        id = "winner_${index}_3",
                        category = "카테고리 $index",
                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
                        tournamentNumb = 3
                    ),
                    CardListItem.WinnerCard(
                        id = "winner_${index}_4",
                        category = "카테고리 $index",
                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
                        tournamentNumb = 4
                    )
                )
            )
        }

//        val cardListItems = categories.flatMap { it.cardListItems }

        // 데이터를 어댑터에 설정
        adapter.submitList(categories)
        initFragmentResultListener()
    }

    private fun initChipGroup() {
        for (tag in Tag.values()) {
            if (tag == Tag.ALL) {
                continue
            }

            val chip = createChip(requireContext(), tag.strValue)
            if (tag == Tag.BEAUTY) { // 화면 최초 진입 시 전체 태그가 클릭된 상태여야함
                chip.setTagStyle(isBlackBackground = true)
                selectedChip = chip
            }
            chip.setOnClickListener { _ ->
                selectedChip?.setTagStyle(isBlackBackground = false)
                chip.setTagStyle(isBlackBackground = true)
                selectedChip = chip
            }
            bind {
                chipGroup.addView(chip)
            }
        }
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY_VOTING_ROOM_ENTER,
            viewLifecycleOwner
        ) { _, result ->
            val confirmedId = result.getString(RankingVotingDialogFragment.RESULT_CONFIRM_KEY)
            val canceledId = result.getString(RankingVotingDialogFragment.RESULT_CANCEL_KEY)

            when {
                confirmedId != null -> {
                    // category를 bundle로 넘겨줘야한다
                    val bundle = Bundle().apply {
                        putString("selectedCategory", categoryTitle)
                    }
                    findNavController().navigate(R.id.fragmentVoting, bundle)
                }

                canceledId != null -> {
                }

                else -> {
                }
            }
        }
    }

    companion object {
        const val REQUEST_KEY_VOTING_ROOM_ENTER = "REQUEST_KEY_VOTING_ROOM_ENTER"
    }
}
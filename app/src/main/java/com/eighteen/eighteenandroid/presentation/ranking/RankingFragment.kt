package com.eighteen.eighteenandroid.presentation.ranking

import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.databinding.FragmentRankingBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.createChip
import com.eighteen.eighteenandroid.presentation.common.setTagStyle
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem
import com.eighteen.eighteenandroid.presentation.ranking.model.RankingCategory
import com.google.android.material.chip.Chip

class RankingFragment : BaseFragment<FragmentRankingBinding>(FragmentRankingBinding::inflate) {
    private var selectedChip: Chip? = null
    override fun initView() {
        val adapter = RankingAdapter()
        initChipGroup()


        binding.rvRanking.run {
            this.adapter = adapter
        }
        
        val categoryTitles = listOf("운동", "뷰티", "공부", "예술", "게임")

        val categories = (0..9).map { index ->
            val titleIndex = index % categoryTitles.size
            RankingCategory.Category(
                id = index.toString(),
                categoryTitle = categoryTitles[titleIndex],
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

        // 데이터를 어댑터에 설정
        adapter.submitList(categories)
    }

    private fun initChipGroup() {
        for (tag in Tag.values()) {
            val chip = createChip(requireContext(), tag.strValue)
            if (tag == Tag.ALL) { // 화면 최초 진입 시 전체 태그가 클릭된 상태여야함
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
}
package com.eighteen.eighteenandroid.presentation.ranking

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentRankingBinding
import com.eighteen.eighteenandroid.domain.usecase.GetTournamentCategoryInfoUseCase
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.ranking.RankingResultFragment.Companion.ARGUMENT_KEY_CATEGORY
import com.eighteen.eighteenandroid.presentation.ranking.RankingResultFragment.Companion.ARGUMENT_KEY_TOURNAMENT_NO
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem
import com.eighteen.eighteenandroid.presentation.ranking.model.RankingCategory
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RankingFragment : BaseFragment<FragmentRankingBinding>(FragmentRankingBinding::inflate) {
    private var selectedChip: Chip? = null
    private lateinit var categories: List<RankingCategory.Category>
    private var categoryTitle: String? = null

    @Inject
    lateinit var getTournamentCategoryInfoUseCase: GetTournamentCategoryInfoUseCase
    private val rankingViewModel by viewModels<RankingViewModel>()

    override fun initView() {
//        val adapter = RankingAdapter()

        val adapter = RankingAdapter(onVoteCardClick = { voteCard ->
            val category = categories.find { category ->
                category.cardListItems.any { it.id == voteCard.id }
            }
            categoryTitle = category?.categoryTitle ?: "뷰티"

            val votingDialog = RankingVotingDialogFragment.newInstance(
                requestKey = REQUEST_KEY_VOTING_ROOM_ENTER,
                id = voteCard.id.toString(),
                categoryTitle = categoryTitle ?: "뷰티"
            )
            showDialogFragment(votingDialog)
//            requestWithRequiredLogin { showDialogFragment(votingDialog) }
        }, onWinnerCardClick = { winnerCard ->
            findNavController().navigate(
                R.id.action_fragmentRanking_to_fragmentRankingResult,
                bundleOf(
                    ARGUMENT_KEY_TOURNAMENT_NO to winnerCard.id,    // 토너먼트 번호
                    ARGUMENT_KEY_CATEGORY to categoryTitle                // 카테고리 이름
                )
            )
        }
        )

        binding.rvRanking.run {
            this.adapter = adapter
        }


//        val categoryList = listOf("운동", "뷰티", "공부", "예술", "게임")
//
//        categories = (0..9).map { index ->
//            val titleIndex = index % categoryList.size
//            RankingCategory.Category(
//                id = index,
//                categoryTitle = categoryList[titleIndex],
//                cardListItems = listOf(
//                    CardListItem.VoteCard(
//                        id = index,
//                        category = "카테고리 $index",
//                        illustrationUrl = "https://png.pngtree.com/thumb_back/fw800/back_our/20190625/ourmid/pngtree-beautiful-nice-sky-blue-background-image_260273.jpg"
//                    ),
//                    CardListItem.WinnerCard(
//                        id = index,
//                        round = index,
//                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
//                    ),
//                    CardListItem.WinnerCard(
//                        id = index,
//                        round = index,
//                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
//                    ),
//                    CardListItem.WinnerCard(
//                        id = index,
//                        round = index,
//                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
//                    ),
//                    CardListItem.WinnerCard(
//                        id = index,
//                        round = index,
//                        imageUrl = "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
//                    )
//                )
//            )
//        }



//        val cardListItems = categories.flatMap { it.cardListItems }
//
////        데이터를 어댑터에 설정
//        adapter.submitList(categories)
        observeViewModel(adapter)
        initFragmentResultListener()
    }

    private fun observeViewModel(adapter: RankingAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                rankingViewModel.categories.collect { modelState ->
                    when (modelState) {
                        is ModelState.Success -> {
                            val originalList = modelState.data
                            categories = originalList?.filterIsInstance<RankingCategory.Category>()
                                ?: emptyList()
                            val updatedList = originalList?.map { category ->
                                if (category is RankingCategory.Category) {
                                    // 기존 cardListItems에 VoteCard 추가
                                    val updatedCardListItems = mutableListOf<CardListItem>().apply {
                                        add(
                                            CardListItem.VoteCard(
                                                id = -1, // 고유 ID (중복되지 않게 설정)
                                                category = "Example Category", // 카테고리 이름
                                                illustrationUrl = "https://example.com/image.jpg" // 예시 URL
                                            )
                                        )
                                        addAll(category.cardListItems)
                                    }

                                    // updatedCardListItems로 Category 객체를 복사 생성
                                    category.copy(cardListItems = updatedCardListItems)
                                } else {
                                    category // 다른 타입은 그대로 유지
                                }
                            }
                            adapter.submitList(updatedList)
                        }

                        is ModelState.Loading -> {
                            // 로딩 중 처리 (필요 시)
                        }

                        is ModelState.Error -> {
                            // 에러 처리 (필요 시)
                            Log.e(
                                "RankingFragment",
                                "Error loading categories",
                                modelState.throwable
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }

//    private fun initChipGroup() {
//        for (tag in Tag.values()) {
//            if (tag == Tag.ALL) {
//                continue
//            }
//
//            val chip = createChip(requireContext(), tag.strValue)
//            if (tag == Tag.BEAUTY) { // 화면 최초 진입 시 전체 태그가 클릭된 상태여야함
//                chip.setTagStyle(isBlackBackground = true)
//                selectedChip = chip
//            }
//            chip.setOnClickListener { _ ->
//                selectedChip?.setTagStyle(isBlackBackground = false)
//                chip.setTagStyle(isBlackBackground = true)
//                selectedChip = chip
//            }
//            bind {
//                chipGroup.addView(chip)
//            }
//        }
//    }

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
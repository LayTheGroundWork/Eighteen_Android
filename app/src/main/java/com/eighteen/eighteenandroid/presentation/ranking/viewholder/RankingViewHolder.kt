package com.eighteen.eighteenandroid.presentation.ranking.viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.databinding.ItemRankingBinding
import com.eighteen.eighteenandroid.presentation.ranking.cardList.CardListAdapter
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem
import com.eighteen.eighteenandroid.presentation.ranking.model.RankingCategory

class RankingViewHolder(
    private val binding: ItemRankingBinding,
    private val onVoteCardClick: (CardListItem.VoteCard) -> Unit,
    private val onWinnerCardClick: (CardListItem.WinnerCard) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private val cardListAdapter = CardListAdapter(onVoteCardClick, onWinnerCardClick)

    init {
        binding.rvRanking.apply {
            adapter = cardListAdapter
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun onBind(model: RankingCategory.Category) {
        binding.tvCategoryTitle.text = model.categoryTitle

        cardListAdapter.submitList(model.cardListItems)
    }
}
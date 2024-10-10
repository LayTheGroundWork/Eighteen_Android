package com.eighteen.eighteenandroid.presentation.ranking.cardList.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.eighteen.eighteenandroid.databinding.ItemRankingVoteBinding
import com.eighteen.eighteenandroid.databinding.ItemRankingWinnerBinding
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem

sealed class CardListViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun onBind(cardListModel: CardListItem) {}

    class Vote(private val binding: ItemRankingVoteBinding) : CardListViewHolder(binding) {
        private var voteModel: CardListItem.VoteCard? = null

        override fun onBind(cardListModel: CardListItem) {
            voteModel = cardListModel as? CardListItem.VoteCard

            Glide.with(binding.ivVote.context)
                .load(voteModel?.illustrationUrl)
                .into(binding.ivVote)
        }
    }

    class Winner(private val binding: ItemRankingWinnerBinding) : CardListViewHolder(binding) {
        private var winnerModel: CardListItem.WinnerCard? = null

        override fun onBind(cardListModel: CardListItem) {
            winnerModel = cardListModel as? CardListItem.WinnerCard

            Glide.with(binding.ivWinner.context)
                .load(winnerModel?.imageUrl)
                .into(binding.ivWinner)

            binding.tvVote.text = "${winnerModel?.tournamentNumb}회차 우승자"
        }
    }
}
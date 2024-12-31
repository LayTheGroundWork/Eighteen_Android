package com.eighteen.eighteenandroid.presentation.ranking.cardList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.eighteen.eighteenandroid.databinding.ItemRankingVoteBinding
import com.eighteen.eighteenandroid.databinding.ItemRankingWinnerBinding
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem
import com.eighteen.eighteenandroid.presentation.ranking.cardList.viewholder.CardListViewHolder

class CardListAdapter(
    private val onVoteCardClick: (CardListItem.VoteCard) -> Unit,
    private val onWinnerCardClick: (CardListItem.WinnerCard) -> Unit
) : ListAdapter<CardListItem, CardListViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListViewHolder {
        val context = parent.context
        val inflate = LayoutInflater.from(context)

        fun <VB : ViewBinding> inflaterBinding(bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB) =
            bindingInflater.invoke(inflate, parent, false)
        return when (viewType) {
            ITEM_TYPE_VOTE -> {
                val binding = inflaterBinding(ItemRankingVoteBinding::inflate)
                CardListViewHolder.Vote(binding).apply {
                    binding.root.setOnClickListener {
                        val voteCard = getItem(adapterPosition) as? CardListItem.VoteCard
                        voteCard?.let { onVoteCardClick(it) }
                    }
                }
            }

            ITEM_TYPE_WINNER -> {
                val binding = inflaterBinding(ItemRankingWinnerBinding::inflate)
                CardListViewHolder.Winner(binding).apply {
                    binding.root.setOnClickListener {
                        val winnerCard = getItem(adapterPosition) as? CardListItem.WinnerCard
                        winnerCard?.let { onWinnerCardClick(it) }
                    }
                }
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: CardListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CardListItem.VoteCard -> ITEM_TYPE_VOTE
            is CardListItem.WinnerCard -> ITEM_TYPE_WINNER
        }
    }

    companion object {
        private const val ITEM_TYPE_VOTE = 1
        private const val ITEM_TYPE_WINNER = 2

        private val diffUtil = object : DiffUtil.ItemCallback<CardListItem>() {
            override fun areContentsTheSame(oldItem: CardListItem, newItem: CardListItem): Boolean {
                return oldItem.areItemsTheSame(newItem)
            }

            override fun areItemsTheSame(oldItem: CardListItem, newItem: CardListItem): Boolean {
                return oldItem.areItemsTheSame(newItem)
            }
        }
    }
}
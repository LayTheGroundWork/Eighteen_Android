package com.eighteen.eighteenandroid.presentation.ranking

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eighteen.eighteenandroid.databinding.ItemRankingResultBinding
import com.eighteen.eighteenandroid.domain.model.UserRankInfo

class RankingResultAdapter: ListAdapter<UserRankInfo, RankingResultAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<UserRankInfo>() {
            override fun areItemsTheSame(oldItem: UserRankInfo, newItem: UserRankInfo): Boolean {
                return oldItem.rankerId == newItem.rankerId
            }

            override fun areContentsTheSame(oldItem: UserRankInfo, newItem: UserRankInfo): Boolean {
                return oldItem == newItem
            }

        }
    }
    class ViewHolder(val binding: ItemRankingResultBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(userRankInfo: UserRankInfo) {
            with(binding) {
                tvRanking.text = userRankInfo.rank.toString()
                tvUserName.text = userRankInfo.rankerNickName
                tvUserId.text = userRankInfo.rankerId
                tvUserRankingPercent.text = "${userRankInfo.voteRate}%"

                Glide.with(ivUserRank.context)
                    .load(userRankInfo.profileImageUrl)
                    .into(ivUserRank)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRankingResultBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
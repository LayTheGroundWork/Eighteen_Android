package com.eighteen.eighteenandroid.presentation.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.eighteen.eighteenandroid.databinding.ItemRankingBinding
import com.eighteen.eighteenandroid.presentation.ranking.model.RankingCategory
import com.eighteen.eighteenandroid.presentation.ranking.viewholder.RankingViewHolder

class RankingAdapter() : ListAdapter<RankingCategory, RankingViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        fun <VB : ViewBinding> inflateBinding(bindingInflate: (LayoutInflater, ViewGroup, Boolean) -> VB) =
            bindingInflate.invoke(inflater, parent, false)

        val binding = inflateBinding(ItemRankingBinding::inflate)
        return RankingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.onBind(getItem(position) as RankingCategory.Category)
    }


    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<RankingCategory>() {
            override fun areContentsTheSame(
                oldItem: RankingCategory,
                newItem: RankingCategory
            ): Boolean =
                oldItem.areItemsTheSame(newItem)

            override fun areItemsTheSame(
                oldItem: RankingCategory,
                newItem: RankingCategory
            ): Boolean = oldItem.areContentsTheSame(newItem)
        }
    }
}
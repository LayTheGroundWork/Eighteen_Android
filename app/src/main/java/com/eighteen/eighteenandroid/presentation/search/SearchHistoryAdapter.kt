package com.eighteen.eighteenandroid.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.databinding.ItemSearchHistoryBinding
import com.eighteen.eighteenandroid.domain.model.SearchUserHistory

class SearchHistoryAdapter: ListAdapter<SearchUserHistory, SearchHistoryAdapter.ViewHolder>(diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<SearchUserHistory>() {
            override fun areItemsTheSame(oldItem: SearchUserHistory, newItem: SearchUserHistory): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: SearchUserHistory, newItem: SearchUserHistory): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun updateList(newList: List<SearchUserHistory>) {
        submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchHistoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSearchHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: SearchUserHistory) {
            binding.tvUserName.text = item.userName
            binding.tvUserId.text = item.userId
        }
    }
}
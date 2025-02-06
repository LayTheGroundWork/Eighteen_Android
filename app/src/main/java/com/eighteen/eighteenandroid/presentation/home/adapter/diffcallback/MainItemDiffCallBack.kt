package com.eighteen.eighteenandroid.presentation.home.adapter.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.eighteen.eighteenandroid.domain.model.MainItem

class MainItemDiffCallBack: DiffUtil.ItemCallback<MainItem>() {
    override fun areItemsTheSame(oldItem: MainItem, newItem: MainItem): Boolean {
        return when {
            oldItem is MainItem.HeaderView && newItem is MainItem.HeaderView -> {
                oldItem.text == newItem.text
            }

            oldItem is MainItem.HeaderWithMoreView && newItem is MainItem.HeaderWithMoreView -> {
                oldItem.text == newItem.text
            }

            oldItem is MainItem.UserListView && newItem is MainItem.UserListView -> {
                oldItem.userList == newItem.userList
            }

            oldItem is MainItem.DividerView && newItem is MainItem.DividerView -> {
                true
            }

            oldItem is MainItem.AboutTeenListView && newItem is MainItem.AboutTeenListView -> {
                oldItem.aboutTeenList == newItem.aboutTeenList
            }

            oldItem is MainItem.TournamentListView && newItem is MainItem.TournamentListView -> {
                oldItem.tournamentList == newItem.tournamentList
            }

            oldItem is MainItem.UserView && newItem is MainItem.UserView -> {
                oldItem.user.userId == newItem.user.userId || oldItem.user.uniqueId == newItem.user.uniqueId
            }

            oldItem is MainItem.LoadingView && newItem is MainItem.LoadingView -> {
                true
            }

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: MainItem, newItem: MainItem): Boolean {
        return oldItem == newItem
    }
}
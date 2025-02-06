package com.eighteen.eighteenandroid.presentation.home.adapter.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.eighteen.eighteenandroid.domain.model.User

class UserDiffCallBack() : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uniqueId == newItem.uniqueId || oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
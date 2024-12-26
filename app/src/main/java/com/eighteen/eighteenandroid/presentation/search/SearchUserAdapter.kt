package com.eighteen.eighteenandroid.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemSearchBinding
import com.eighteen.eighteenandroid.domain.model.SearchUser

class SearchUserAdapter(
    private val onHeartClicked: (SearchUser) -> Unit
): ListAdapter<SearchUser, SearchUserAdapter.ViewHolder>(diffCallback) {
    companion object {
        val diffCallback = object: DiffUtil.ItemCallback<SearchUser>() {
            override fun areItemsTheSame(oldItem: SearchUser, newItem: SearchUser): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: SearchUser, newItem: SearchUser): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onHeartClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.onBind(getItem(position))
    }

    fun updateList(newList: List<SearchUser>) {
        submitList(newList)
    }

    class ViewHolder(private val binding: ItemSearchBinding, val onHeartClicked: (SearchUser) -> Unit): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: SearchUser) {
            with(binding) {
                tvUserName.text = item.userName
                tvUserId.text = item.userId

                // 유저 프로필 사진
                Glide.with(ivUser.context)
                    .load(item.profileImageUrl)
                    .into(ivUser)

                // 좋아요 표시
                if(item.likeStatus) {
                    ivHeart.setImageResource(R.drawable.ic_heart_selected)
                } else {
                    ivHeart.setImageResource(R.drawable.ic_heart_unselected)
                }

                // 좋아요 버튼 클릭
                ivHeart.setOnClickListener {
                    onHeartClicked(item)
                }
            }
        }
    }
}
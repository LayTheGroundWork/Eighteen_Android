package com.eighteen.eighteenandroid.presentation.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eighteen.eighteenandroid.databinding.ItemPopularTeenBinding
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.presentation.common.getScreenWidth
import com.eighteen.eighteenandroid.presentation.home.adapter.diffcallback.UserDiffCallBack
import kotlin.math.roundToInt

/**
 *
 * @file TeenAdapter.kt
 * @date 05/08/2024
 * 오늘의 Teen & 또다른 Teen 목록에 대한 RecyclerView Adapter
 */
class PopularUserAdapter(
    private val context: Context,
    private val mainAdapterListener: MainAdapterListener
) : ListAdapter<User, PopularUserAdapter.TeenViewHolder>(UserDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemPopularTeenBinding.inflate(layoutInflater, parent,false)

        return TeenViewHolder(itemBinding, mainAdapterListener)
    }

    override fun onBindViewHolder(holder: TeenViewHolder, position: Int) {
        holder.bind(getItem(position), context)
    }

    class TeenViewHolder(
        private val binding: ItemPopularTeenBinding,
        private val mainAdapterListener: MainAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(user: User, context: Context) {
            val layoutParams = itemView.layoutParams
            val itemWidth = context.getScreenWidth() - (context.getScreenWidth() * 0.07 * 2).roundToInt()
            layoutParams.width = itemWidth          // 0.85
            layoutParams.height = itemWidth * 100 / 85  // 1
            itemView.layoutParams = layoutParams

            binding.run {
                Glide.with(context).load(user.userImage).into(imgTodayTeen)
                tvName.text = "${user.userName}, ${user.userAge}"
                tvSchool.text = user.userSchoolName
                btnLike.isSelected = user.likeStatus        // 좋아요 상태

                imgTodayTeen.setOnClickListener {
                    mainAdapterListener.onUserClicks(user)
                }

                btnChat.setOnClickListener {
                    mainAdapterListener.onUserChatClicks(user)
                }

                btnLike.setOnClickListener {
                    mainAdapterListener.onUserLikeClicks(btnLike, user)
                }

                btnSetting.setOnClickListener {
                    mainAdapterListener.onUserMoreClicks(btnSetting, user)
                }
            }
        }
    }
}
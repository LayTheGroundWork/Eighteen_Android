package com.eighteen.eighteenandroid.presentation.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemPopularTeenBinding
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.presentation.home.adapter.diffcallback.UserDiffCallBack

/**
 *
 * @file TeenAdapter.kt
 * @date 05/08/2024
 * 오늘의 Teen & 또다른 Teen 목록에 대한 RecyclerView Adapter
 */
class TeenAdapter(
    private val context: Context,
    private val mainAdapterListener: MainAdapterListener
) : ListAdapter<User, TeenAdapter.TeenViewHolder>(UserDiffCallBack()) {

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
            binding.run {
                Glide.with(context).load(user.userImage).into(imgTodayTeen)
                tvName.text = "${user.userName}, ${user.userAge}"
                tvSchool.text = user.userSchoolName

                binding.imgTodayTeen.setOnClickListener {
                    mainAdapterListener.onUserClicks(user)
                }

                btnSetting.setOnClickListener {
                    mainAdapterListener.onUserMoreClicks(btnSetting, user)
                }
            }
        }
    }
}
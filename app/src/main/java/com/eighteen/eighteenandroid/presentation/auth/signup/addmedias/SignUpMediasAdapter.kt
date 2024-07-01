package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.eighteen.eighteenandroid.databinding.ItemSignUpMediaBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.model.SignUpMediaItemModel
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.viewholder.SignUpMediaViewHolder

class SignUpMediasAdapter(private val clickListener: SignUpAddMediasClickListener) :
    ListAdapter<SignUpMediaItemModel, SignUpMediaViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignUpMediaViewHolder {
        val binding =
            ItemSignUpMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SignUpMediaViewHolder(binding = binding, clickListener = clickListener)
    }

    override fun onBindViewHolder(holder: SignUpMediaViewHolder, position: Int) {
        holder.onBind(getItem(position), position)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<SignUpMediaItemModel>() {
            override fun areItemsTheSame(
                oldItem: SignUpMediaItemModel,
                newItem: SignUpMediaItemModel
            ): Boolean = true

            override fun areContentsTheSame(
                oldItem: SignUpMediaItemModel,
                newItem: SignUpMediaItemModel
            ): Boolean = oldItem.areContentsTheSame(newItem)
        }
    }
}
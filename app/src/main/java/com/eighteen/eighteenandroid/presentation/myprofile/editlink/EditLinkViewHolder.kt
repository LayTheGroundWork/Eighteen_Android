package com.eighteen.eighteenandroid.presentation.myprofile.editlink

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.databinding.ItemLinkBinding
import com.eighteen.eighteenandroid.domain.model.SnsLink

class EditLinkViewHolder(
    private val binding: ItemLinkBinding,
    private val onClickRemove: (Int) -> Unit = {}
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(data: SnsLink, position: Int) {
        with(binding) {
            tvName.text = data.name
            listOf(tvName, vDivider).forEach {
                it.isVisible = data.name.isNullOrEmpty().not()
            }
            tvLink.text = data.linkUrl
            tvBtnRemove.setOnClickListener {
                onClickRemove.invoke(position)
            }
        }
    }
}
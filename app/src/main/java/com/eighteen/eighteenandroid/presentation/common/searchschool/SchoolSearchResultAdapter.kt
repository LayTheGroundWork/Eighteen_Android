package com.eighteen.eighteenandroid.presentation.common.searchschool

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.eighteen.eighteenandroid.databinding.ItemSchoolSearchResultBinding
import com.eighteen.eighteenandroid.databinding.LayoutLoadingBinding
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.presentation.common.searchschool.model.SchoolSearchModel
import com.eighteen.eighteenandroid.presentation.common.searchschool.viewholder.SchoolSearchLoadingViewHolder
import com.eighteen.eighteenandroid.presentation.common.searchschool.viewholder.SchoolSearchResultViewHolder
import com.eighteen.eighteenandroid.presentation.common.searchschool.viewholder.SchoolSearchViewHolder

class SchoolSearchResultAdapter(private val onClickSchool: (School) -> Unit) :
    ListAdapter<SchoolSearchModel, SchoolSearchViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SchoolSearchViewHolder {
        return when (viewType) {
            ITEM_TYPE_RESULT -> {
                val binding =
                    ItemSchoolSearchResultBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                SchoolSearchResultViewHolder(
                    binding = binding,
                    onClickSchool = onClickSchool
                )
            }
            else -> {
                val binding =
                    LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SchoolSearchLoadingViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: SchoolSearchViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SchoolSearchModel.SchoolModel -> ITEM_TYPE_RESULT
            else -> ITEM_TYPE_LOADING
        }
    }

    companion object {
        private const val ITEM_TYPE_RESULT = 1
        private const val ITEM_TYPE_LOADING = 2
        private val diffUtil = object : DiffUtil.ItemCallback<SchoolSearchModel>() {
            override fun areItemsTheSame(
                oldItem: SchoolSearchModel,
                newItem: SchoolSearchModel
            ): Boolean = oldItem.areItemsTheSame(newItem)

            override fun areContentsTheSame(
                oldItem: SchoolSearchModel,
                newItem: SchoolSearchModel
            ): Boolean = oldItem.areContentsTheSame(newItem)
        }
    }
}
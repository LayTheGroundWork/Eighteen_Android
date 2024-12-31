package com.eighteen.eighteenandroid.presentation.common.searchschool.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.presentation.common.searchschool.model.SchoolSearchModel

abstract class SchoolSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(model: SchoolSearchModel)
}
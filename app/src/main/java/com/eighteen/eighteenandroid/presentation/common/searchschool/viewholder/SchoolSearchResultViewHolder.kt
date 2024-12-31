package com.eighteen.eighteenandroid.presentation.common.searchschool.viewholder

import com.eighteen.eighteenandroid.databinding.ItemSchoolSearchResultBinding
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.presentation.common.searchschool.model.SchoolSearchModel

class SchoolSearchResultViewHolder(
    private val binding: ItemSchoolSearchResultBinding,
    private val onClickSchool: (School) -> Unit
) : SchoolSearchViewHolder(binding.root) {

    override fun onBind(model: SchoolSearchModel) {
        (model as? SchoolSearchModel.SchoolModel)?.school?.let { school ->
            with(binding) {
                root.setOnClickListener {
                    onClickSchool.invoke(school)
                }
                tvSchoolName.text = school.name
                tvAddress.text = school.address
            }
        }
    }
}
package com.eighteen.eighteenandroid.presentation.common.searchschool.model

import com.eighteen.eighteenandroid.domain.model.School

sealed interface SchoolSearchModel {
    fun areItemsTheSame(other: SchoolSearchModel): Boolean
    fun areContentsTheSame(other: SchoolSearchModel): Boolean
    data class SchoolModel(val school: School) : SchoolSearchModel {
        override fun areItemsTheSame(other: SchoolSearchModel): Boolean =
            other is SchoolModel && this.school == other.school

        override fun areContentsTheSame(other: SchoolSearchModel): Boolean = this == other

    }

    object Loading : SchoolSearchModel {
        override fun areItemsTheSame(other: SchoolSearchModel): Boolean = other is Loading

        override fun areContentsTheSame(other: SchoolSearchModel): Boolean = other is Loading
    }
}
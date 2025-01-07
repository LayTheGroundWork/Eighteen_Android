package com.eighteen.eighteenandroid.presentation.dialog.datepicker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DatePickerDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Parcelable

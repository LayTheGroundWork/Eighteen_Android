package com.eighteen.eighteenandroid.presentation.mediadetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MediaDetailViewModel(startPosition: Int = 0) : ViewModel() {
    var selectedIndex: Int = startPosition


    class Factory(private val startPosition: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MediaDetailViewModel(startPosition) as T
        }
    }
}
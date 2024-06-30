package com.eighteen.eighteenandroid.presentation.profiledetail

import androidx.lifecycle.ViewModel
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileDetailViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<ProfileDetailModel>>(emptyList())
    val items: StateFlow<List<ProfileDetailModel>> get() = _items

    private val _showAllItems = MutableStateFlow(false)
    val showAllItems: StateFlow<Boolean> get() = _showAllItems

    companion object {
        const val ITEM_COUNT_THRESHOLD = 2
    }

    fun setItems(newItems: List<ProfileDetailModel>) {
        _items.value = newItems
    }

    fun toggleItems() {
        _showAllItems.value = !_showAllItems.value
    }

    fun getItemCount(): Int {
        val itemList = _items.value
        return if (_showAllItems.value || itemList.isEmpty() || itemList.size <= ITEM_COUNT_THRESHOLD) {
            itemList.size
        } else {
            ITEM_COUNT_THRESHOLD
        }
    }


}
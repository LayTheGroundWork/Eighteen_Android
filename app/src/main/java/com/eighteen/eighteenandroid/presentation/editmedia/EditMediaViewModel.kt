package com.eighteen.eighteenandroid.presentation.editmedia

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eighteen.eighteenandroid.presentation.common.livedata.Event
import com.eighteen.eighteenandroid.presentation.editmedia.model.EditMediaResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditMediaViewModel : ViewModel() {

    private val _mediaUriStringStateFlow = MutableStateFlow("")
    val mediaUriStringStateFlow: StateFlow<String> = _mediaUriStringStateFlow

    private val _cropAreaBitmapStateFlow = MutableStateFlow<Bitmap?>(null)
    val cropAreaBitmapStateFlow: StateFlow<Bitmap?> = _cropAreaBitmapStateFlow

    private val _editResultEventLiveData = MutableLiveData<Event<EditMediaResult>>()
    val editResultEventLiveData: LiveData<Event<EditMediaResult>> = _editResultEventLiveData

    fun setMediaUriString(uriString: String) {
        _mediaUriStringStateFlow.value = uriString
    }

    fun setAndRecycleCropAreaBitmap(bitmap: Bitmap) {
        _cropAreaBitmapStateFlow.value = bitmap
    }

    fun setEditResultEvent(result: EditMediaResult) {
        _editResultEventLiveData.value = Event(result)
    }

    override fun onCleared() {
        _cropAreaBitmapStateFlow.value?.recycle()
        super.onCleared()
    }
}
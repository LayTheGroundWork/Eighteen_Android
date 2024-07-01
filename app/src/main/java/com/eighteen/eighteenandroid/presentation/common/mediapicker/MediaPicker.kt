package com.eighteen.eighteenandroid.presentation.common.mediapicker

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MediaPicker {
    private val pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    constructor(activity: AppCompatActivity, callback: (Uri?) -> Unit) {
        pickMedia =
            activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                callback.invoke(uri)
            }
        pickMedia.unregister()
    }

    constructor(fragment: Fragment, callback: (Uri?) -> Unit) {
        pickMedia =
            fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                callback.invoke(uri)
            }
    }

    fun openMediaPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }
}
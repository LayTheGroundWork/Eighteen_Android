package com.eighteen.eighteenandroid.presentation.editmedia

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.viewModelsByBackStackEntryId

/**
 * @property popDestinationId : ViewModel 공유를 위한 호출자 fragment의 navigation id,
 * ViewModel 공유를 위해 반드시 넣어야 함
 */
abstract class BaseEditMediaFragment<VB : ViewBinding>(bindingFactory: (LayoutInflater) -> VB) :
    BaseFragment<VB>(bindingFactory = bindingFactory) {
    protected val popDestinationId get() = arguments?.getInt(EDIT_MEDIA_POP_DESTINATION_ID_KEY, -1)

    protected val editMediaViewModel by viewModelsByBackStackEntryId<EditMediaViewModel>(
        destinationIdProducer = { popDestinationId ?: -1 }
    )

    companion object {
        const val EDIT_MEDIA_POP_DESTINATION_ID_KEY = "edit_media_pop_destination_id_key"
        const val EDIT_MEDIA_RESULT_KEY = "edit_media_result_key"
    }
}
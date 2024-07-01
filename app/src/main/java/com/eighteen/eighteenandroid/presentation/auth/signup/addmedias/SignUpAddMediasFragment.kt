package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.safeLet
import com.eighteen.eighteenandroid.databinding.FragmentSignUpAddMediasBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.model.SignUpMediaItemModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpEditMediaAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpMedia
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.common.getMimeTypeFromUri
import com.eighteen.eighteenandroid.presentation.common.mediapicker.MediaPicker
import kotlinx.coroutines.launch

class SignUpAddMediasFragment :
    BaseSignUpContentFragment<FragmentSignUpAddMediasBinding>(FragmentSignUpAddMediasBinding::inflate) {
    override val onMovePrevPageAction: () -> Unit = {
        signUpViewModelContentInterface.clearMediaResultStateFlow()
        super.onMovePrevPageAction()
    }

    override val onMoveNextPageAction: () -> Unit = {
        findNavController().navigate(R.id.action_fragmentSignUpAddMedias_to_fragmentSignUpCompleted)
    }
    override val progress: Int = 100
    override val signUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = true,
        size = SignUpNextButtonModel.Size.NORMAL,
        buttonText = SignUpNextButtonModel.ButtonText.PASS
    )

    private val mediaPicker = MediaPicker(this) { uri ->
        safeLet(uri, context) { nonNullUri, context ->
            getMimeTypeFromUri(nonNullUri, context)?.let { typeString ->
                val uriString = nonNullUri.toString()
                if (typeString.startsWith("image")) SignUpEditMediaAction.EditImage(uriString)
                else if (typeString.startsWith("video")) SignUpEditMediaAction.EditVideo(uriString)
                else null
            }?.also { action ->
                signUpViewModelContentInterface.setEditMediaAction(action)
            }
        }
    }

    private val clickListener = object : SignUpAddMediasClickListener {
        override fun onClickAddMedia(position: Int) {
            mediaPicker.openMediaPicker()
        }

        override fun onClickMedia() {
            Log.d("TESTLOG", "onClickMedia")
            //TODO 미디어 클릭 시
        }
    }

    override fun initView() {
        initRecyclerView()
        initStateFlow()
    }

    private fun initRecyclerView() {
        bind {
            rvMedias.adapter = SignUpMediasAdapter(clickListener = clickListener)
            rvMedias.addItemDecoration(SignUpMediasItemDecoration())
        }
    }

    private fun initStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModelContentInterface.mediasStateFlow.collect {
                    (binding.rvMedias.adapter as? SignUpMediasAdapter)?.run {
                        val mediaItemModels = createSignUpMediaItemModels(it)
                        submitList(mediaItemModels)
                    }
                }

            }
        }
    }

    private fun createSignUpMediaItemModels(signUpMedias: List<SignUpMedia>): List<SignUpMediaItemModel> {
        val items = mutableListOf<SignUpMediaItemModel>()
        if (signUpMedias.isEmpty()) {
            items.add(SignUpMediaItemModel.RefEmpty)
            items.addAll(List(MINIMUM_DISPLAY_ITEM_COUNT - 1) { SignUpMediaItemModel.Empty })
        } else {
            val mediaItemModels = signUpMedias.map {
                when (it) {
                    is SignUpMedia.Image -> SignUpMediaItemModel.Image(imageBitmap = it.imageBitmap)
                    is SignUpMedia.Video -> SignUpMediaItemModel.Video(thumbnailBitmap = it.thumbnailBitmap)
                }
            }
            items.addAll(mediaItemModels)
        }
        if (items.size < MAXIMUM_DISPLAY_ITEM_COUNT) items.add(SignUpMediaItemModel.Empty)
        return items
    }

    companion object {
        private const val MINIMUM_DISPLAY_ITEM_COUNT = 2
        private const val MAXIMUM_DISPLAY_ITEM_COUNT = 10
    }
}
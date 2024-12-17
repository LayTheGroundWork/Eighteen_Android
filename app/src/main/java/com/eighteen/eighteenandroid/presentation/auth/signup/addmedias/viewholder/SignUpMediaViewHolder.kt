package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemSignUpMediaBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.SignUpAddMediasClickListener
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.model.SignUpMediaItemModel
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader

class SignUpMediaViewHolder(
    private val binding: ItemSignUpMediaBinding,
    private val clickListener: SignUpAddMediasClickListener,
) : ViewHolder(binding.root) {

    fun onBind(model: SignUpMediaItemModel, position: Int) {
        bindImage(model = model)
        bindClickListener(model = model, position = position)
    }

    private fun bindImage(model: SignUpMediaItemModel) {
        with(binding) {
            when (model) {
                is SignUpMediaItemModel.Empty -> ivMedia.setImageResource(R.drawable.bg_add_media_empty)
                is SignUpMediaItemModel.Image -> ImageLoader.get()
                    .loadBitmapCenterCrop(ivMedia, model.imageBitmap)
                is SignUpMediaItemModel.Video -> ImageLoader.get()
                    .loadUrlCenterCrop(ivMedia, url = model.uriString)
            }

            ivBtnDelete.isVisible = model !is SignUpMediaItemModel.Empty
            ivVideoIcon.isVisible = model is SignUpMediaItemModel.Video
            ivBtnSetMainMedia.isVisible = model !is SignUpMediaItemModel.Empty
            ivBtnSetMainMedia.alpha = if (model.isMain) 1f else 0.2f
        }

    }

    private fun bindClickListener(model: SignUpMediaItemModel, position: Int) {
        binding.ivMedia.setOnClickListener {
            if (model is SignUpMediaItemModel.Empty) clickListener.onClickAddMedia()
        }
        binding.ivBtnDelete.setOnClickListener {
            clickListener.onClickRemove(position = position)
        }
        binding.ivBtnSetMainMedia.setOnClickListener {
            clickListener.onClickSetMainMedia(position = position)
        }
    }
}
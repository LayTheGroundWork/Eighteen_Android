package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.viewholder

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemSignUpMediaBinding
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.SignUpAddMediasClickListener
import com.eighteen.eighteenandroid.presentation.auth.signup.addmedias.model.SignUpMediaItemModel
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader

//TODO 디자인 리소스 적용
class SignUpMediaViewHolder(
    private val binding: ItemSignUpMediaBinding,
    private val clickListener: SignUpAddMediasClickListener,
) : ViewHolder(binding.root) {

    fun onBind(model: SignUpMediaItemModel, position: Int) {
        bindImage(model = model)
        bindClickListener(model = model, position = position)
    }

    //TODO 디자인 가이드 적용
    private fun bindImage(model: SignUpMediaItemModel) {
        when (model) {
            is SignUpMediaItemModel.RefEmpty -> binding.ivMedia.setImageResource(R.drawable.bg_about_teen)
            is SignUpMediaItemModel.Empty -> binding.ivMedia.setImageResource(R.drawable.ic_launcher_background)
            is SignUpMediaItemModel.Image -> ImageLoader.get()
                .loadBitmap(binding.ivMedia, model.imageBitmap)
            is SignUpMediaItemModel.Video -> ImageLoader.get()
                .loadBitmap(binding.ivMedia, model.thumbnailBitmap)
        }
    }

    private fun bindClickListener(model: SignUpMediaItemModel, position: Int) {
        binding.ivMedia.setOnClickListener {
            when (model) {
                is SignUpMediaItemModel.RefEmpty -> clickListener.onClickAddMedia(position)
                is SignUpMediaItemModel.Empty -> clickListener.onClickAddMedia(position)
                is SignUpMediaItemModel.Image -> clickListener.onClickMedia()
                is SignUpMediaItemModel.Video -> clickListener.onClickMedia()
            }
        }
    }
}
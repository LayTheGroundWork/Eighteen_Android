package com.eighteen.eighteenandroid.presentation.myprofile.viewholder

import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemMyProfileIntroduceBinding
import com.eighteen.eighteenandroid.presentation.myprofile.MyProfileClickListener
import com.eighteen.eighteenandroid.presentation.myprofile.model.MyProfileItem

class MyProfileIntroduceViewHolder(
    private val clickListener: MyProfileClickListener,
    private val binding: ItemMyProfileIntroduceBinding
) : BaseMyProfileViewHolder<MyProfileItem.Introduce, ItemMyProfileIntroduceBinding>(binding) {
    override fun onBind(model: MyProfileItem.Introduce) {
        with(binding) {
            ivBtnEditIntroduce.setOnClickListener {
                clickListener.onClickEditIntroduce()
            }
            tvMbti.text = model.mbti
            tvDescription.text =
                model.description ?: context.getString(R.string.my_profile_introduce_empty)
        }
    }
}
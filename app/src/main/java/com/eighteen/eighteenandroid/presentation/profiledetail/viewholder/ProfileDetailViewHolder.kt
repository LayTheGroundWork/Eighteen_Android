package com.eighteen.eighteenandroid.presentation.profiledetail.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailBadgeAndTeenBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailImagesWithLikeBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailInfoBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailIntroductionBinding
import com.eighteen.eighteenandroid.databinding.ItemQnaBinding
import com.eighteen.eighteenandroid.databinding.ItemQnaTitleBinding
import com.eighteen.eighteenandroid.databinding.ItemSeeMoreBinding
import com.eighteen.eighteenandroid.presentation.profiledetail.ProfileDetailViewModel
import com.eighteen.eighteenandroid.presentation.profiledetail.ViewPagerAdapter
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import com.google.android.material.tabs.TabLayoutMediator

sealed class ProfileDetailViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    open fun onBind(profileDetailModel: ProfileDetailModel) {}

    class Info(private val binding: ItemProfileDetailInfoBinding) :
        ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {
            val profileInfo = profileDetailModel as? ProfileDetailModel.ProfileInfo
            profileInfo?.let {
                binding.tvName.text = it.name
                binding.tvAge.text = "${it.age}세"
                binding.tvSchool.text = "${it.school}, "
            }
        }
    }

    class Images(val binding: ItemProfileDetailImagesWithLikeBinding, private val onPageChangeCallback: (Int) -> Unit) :
        ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {
            val profileImages = profileDetailModel as? ProfileDetailModel.ProfileImages
            profileImages?.let {
                val adapter = ViewPagerAdapter(it.imageUrl)
                binding.viewPager.adapter = adapter
                TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

                binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        // 페이지 변경 시 콜백을 통해 currentPosition 값을 업데이트
                        onPageChangeCallback(position)
                    }
                })
            }
        }
    }

    class BadgeAndTeen(private val binding: ItemProfileDetailBadgeAndTeenBinding) :
        ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {

            val badgeAndTeen = profileDetailModel as? ProfileDetailModel.BadgeAndTeen
            badgeAndTeen?.let {
                binding.tvBadgeCount.text = "${it.badgeCount}개"
                binding.tvTeenAward.text = it.teenAward
            }
        }
    }

    class Introduction(private val binding: ItemProfileDetailIntroductionBinding) :
        ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {

            val introduction = profileDetailModel as? ProfileDetailModel.Introduction
            introduction?.let {
                binding.chipPersonalityType.text = it.personalityType
                binding.tvIntroduction.text = it.introductionText
            }
        }
    }

    class QnaTitle(private val binding: ItemQnaTitleBinding) : ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {
            super.onBind(profileDetailModel)
        }
    }

    class Qna(val binding: ItemQnaBinding) : ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {
            val qna = profileDetailModel as? ProfileDetailModel.Qna
            qna.let {
                binding.question.text = it?.question
                binding.answer.text = it?.answer
            }
        }
    }

    class QnaToggle(
        val binding: ItemSeeMoreBinding,
        private val toggleItems: (ProfileDetailModel.QnaToggle) -> Unit
    ) :
        ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {
            val toggle = profileDetailModel as? ProfileDetailModel.QnaToggle
            toggle?.let {
                binding.tvSeeMore.text = if (toggle.isExpanded) "접기" else "펼쳐서 보기"
                binding.tvSeeMore.setOnClickListener(View.OnClickListener {
                    toggleItems(toggle)
                })
                binding.ivSeeMore.setOnClickListener(View.OnClickListener {
                    toggleItems(toggle)
                })
            }
        }
    }
}


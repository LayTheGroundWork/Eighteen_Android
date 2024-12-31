package com.eighteen.eighteenandroid.presentation.profiledetail.viewholder

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailBadgeAndTeenBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailImagesWithLikeBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailInfoBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailIntroductionBinding
import com.eighteen.eighteenandroid.databinding.ItemQnaBinding
import com.eighteen.eighteenandroid.databinding.ItemQnaTitleBinding
import com.eighteen.eighteenandroid.databinding.ItemSeeMoreBinding
import com.eighteen.eighteenandroid.presentation.common.getRecyclerViewOrNull
import com.eighteen.eighteenandroid.presentation.common.media3.PlayerManager
import com.eighteen.eighteenandroid.presentation.common.media3.viewpager2.ViewPagerMediaItem
import com.eighteen.eighteenandroid.presentation.profiledetail.ViewPagerAdapter
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import com.google.android.material.tabs.TabLayoutMediator

sealed class ProfileDetailViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    open fun onBind(profileDetailModel: ProfileDetailModel) {}

    open fun onDetached() {}
    open fun onAttached() {}

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

    class Images(
        val binding: ItemProfileDetailImagesWithLikeBinding,
        private val onPageChangeCallbackForImagePosition: (Int) -> Unit,
        private val onPageCallbackForVisibilitySoundIcon: ViewPager2.OnPageChangeCallback,
        private val onLikeClickCallback: () -> Unit,
        private val onClickMedia: (Int, List<ProfileDetailModel.MediaItem>) -> Unit,
        private val playerManager: PlayerManager?
    ) : ProfileDetailViewHolder(binding) {
        init {
            binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d("ProfileDetailViewHolder", "onPageSelected")
                    // 페이지 변경 시 콜백을 통해 currentPosition 값을 업데이트 (스크롤시 ViewPager의 위치 유지)
                    onPageChangeCallbackForImagePosition(position)
                    playCurrent(position)
                }
            })

            binding.viewPager.registerOnPageChangeCallback(onPageCallbackForVisibilitySoundIcon)
            binding.viewPager.offscreenPageLimit = 5
        }

        private fun playCurrent(position: Int) {
            val mediaItem = (binding.viewPager.getRecyclerViewOrNull()
                ?.findViewHolderForAdapterPosition(position) as? ViewPagerMediaItem)
            mediaItem?.let {
                playerManager?.play(it.getMediaInfo())
            }
        }

        override fun onBind(profileDetailModel: ProfileDetailModel) {
            val profileImages = profileDetailModel as? ProfileDetailModel.ProfileImages

            profileImages?.let {
                val adapter = ViewPagerAdapter(
                    it.mediaItems,
                    onClickItem = { position ->
                        playerManager?.pause()
                        onClickMedia(position, it.mediaItems)
                    })
                binding.viewPager.adapter = adapter
                TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

                binding.ivLike.setImageResource(
                    if (it.isLiked) R.drawable.ic_full_heart else R.drawable.ic_empty_heart
                )
                binding.tvLike.text = profileImages.likeCount.toString()

                binding.ivLike.setOnClickListener {
                    onLikeClickCallback()
                }
                binding.viewPager.doOnLayout {
                    playCurrent(binding.viewPager.currentItem)
                }
            }
        }

        override fun onDetached() {
            playerManager?.pause()
        }

        override fun onAttached() {
            playerManager?.resume()
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
            binding.root.post {
                if (qna?.question.isNullOrEmpty()) {
                    binding.question.isVisible = false
                    binding.answer.isVisible = false
                    binding.emptyTextView.isVisible = true
                    binding.root.background = null
                    binding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.background_color
                        )
                    )
                    binding.root.setPadding(0, 0, 0, 0)
                } else {
                    // qna가 null이 아닌 경우 처리
                    binding.question.text = qna?.question
                    binding.answer.text = qna?.answer
                }
            }
        }
    }

    class QnaToggle(
        val binding: ItemSeeMoreBinding,
        private val onQnaToggleCallback: () -> Unit
    ) : ProfileDetailViewHolder(binding) {
        override fun onBind(profileDetailModel: ProfileDetailModel) {
            val toggle = profileDetailModel as? ProfileDetailModel.QnaToggle
            toggle?.let {
                binding.tvSeeMore.text = if (toggle.isExpanded) "접기" else "펼쳐서 보기"
                binding.tvSeeMore.setOnClickListener(View.OnClickListener {
                    onQnaToggleCallback()
                })
                binding.ivSeeMore.setOnClickListener(View.OnClickListener {
                    onQnaToggleCallback()
                })
            }
        }
    }
}


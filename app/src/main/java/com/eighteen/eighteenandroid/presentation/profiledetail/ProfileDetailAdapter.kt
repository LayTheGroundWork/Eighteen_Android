package com.eighteen.eighteenandroid.presentation.profiledetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailBadgeAndTeenBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailImagesWithLikeBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailInfoBinding
import com.eighteen.eighteenandroid.databinding.ItemProfileDetailIntroductionBinding
import com.eighteen.eighteenandroid.databinding.ItemQnaBinding
import com.eighteen.eighteenandroid.databinding.ItemQnaTitleBinding
import com.eighteen.eighteenandroid.databinding.ItemSeeMoreBinding
import com.eighteen.eighteenandroid.presentation.common.dp2Px
import com.eighteen.eighteenandroid.presentation.common.media3.PlayerManager
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import com.eighteen.eighteenandroid.presentation.profiledetail.viewholder.ProfileDetailViewHolder

class ProfileDetailAdapter(
    private val pageCallbackForVisibilitySoundIcon: ViewPager2.OnPageChangeCallback,
    private val onPageChangeCallbackForImagePosition: (Int) -> Unit,
    private val onLikeChangeCallback: () -> Unit,
    private val onQnaToggleCallback: () -> Unit,
    private val getCurrentPosition: () -> Int,
    private val onClickMedia: (Int, List<ProfileDetailModel.MediaItem>) -> Unit,
    private val playerManager: PlayerManager?
) : ListAdapter<ProfileDetailModel, ProfileDetailViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileDetailViewHolder {
        val context = parent.context
        val inflate = LayoutInflater.from(context)

        fun <VB : ViewBinding> inflaterBinding(bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB) =
            bindingInflater.invoke(inflate, parent, false)
        return when (viewType) {
            ITEM_TYPE_PROFILE_INFO -> {
                val binding = inflaterBinding(ItemProfileDetailInfoBinding::inflate)
                ProfileDetailViewHolder.Info(binding)
            }

            ITEM_TYPE_PROFILE_IMAGES -> {
                val binding = inflaterBinding(ItemProfileDetailImagesWithLikeBinding::inflate)
                ProfileDetailViewHolder.Images(
                    binding,
                    onPageChangeCallbackForImagePosition = onPageChangeCallbackForImagePosition,
                    onLikeClickCallback = onLikeChangeCallback,
                    onPageCallbackForVisibilitySoundIcon = pageCallbackForVisibilitySoundIcon,
                    onClickMedia = onClickMedia,
                    playerManager = playerManager
                )
            }

            ITEM_TYPE_BADGE_AND_TEEN -> {
                val binding = inflaterBinding(ItemProfileDetailBadgeAndTeenBinding::inflate)
                ProfileDetailViewHolder.BadgeAndTeen(binding)
            }

            ITEM_TYPE_INTRODUCTION -> {
                val binding = inflaterBinding(ItemProfileDetailIntroductionBinding::inflate)
                ProfileDetailViewHolder.Introduction(binding)
            }

            ITEM_TYPE_QNA_LIST_TITLE -> {
                val binding = inflaterBinding(ItemQnaTitleBinding::inflate)
                ProfileDetailViewHolder.QnaTitle(binding)
            }

            ITEM_TYPE_QNA -> {
                val binding = inflaterBinding(ItemQnaBinding::inflate)
                ProfileDetailViewHolder.Qna(binding)
            }

            ITEM_TYPE_QNA_TOGGLE -> {
                val binding = inflaterBinding(ItemSeeMoreBinding::inflate)
                ProfileDetailViewHolder.QnaToggle(
                    binding,
                    onQnaToggleCallback = onQnaToggleCallback
                )
            }

            else -> throw IllegalArgumentException("Invalid view type") // 코드 비교 해 볼것
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProfileDetailModel.ProfileInfo -> ITEM_TYPE_PROFILE_INFO
            is ProfileDetailModel.ProfileImages -> ITEM_TYPE_PROFILE_IMAGES
            is ProfileDetailModel.BadgeAndTeen -> ITEM_TYPE_BADGE_AND_TEEN
            is ProfileDetailModel.Introduction -> ITEM_TYPE_INTRODUCTION
            is ProfileDetailModel.QnaListTitle -> ITEM_TYPE_QNA_LIST_TITLE
            is ProfileDetailModel.Qna -> ITEM_TYPE_QNA
            is ProfileDetailModel.QnaToggle -> ITEM_TYPE_QNA_TOGGLE
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onViewDetachedFromWindow(holder: ProfileDetailViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }

    override fun onViewAttachedToWindow(holder: ProfileDetailViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
    }

    override fun onBindViewHolder(holder: ProfileDetailViewHolder, position: Int) {
        holder.onBind(getItem(position))

        if (holder is ProfileDetailViewHolder.Images) {
            holder.binding.viewPager.setCurrentItem(
                getCurrentPosition(),
                false
            )// 구성 변경시 ViewPager의 위치 유지
        }


        if (holder is ProfileDetailViewHolder.Qna) {

            when {
                position == 5 -> {
                    // 첫 번째 Qna 항목의 상단에 radius를 적용
                    holder.binding.root.setBackgroundResource(R.drawable.bg_qna_rounded_top)
                }

                position == itemCount - 1 && itemCount - 5 <= ProfileDetailViewModel.ITEM_COUNT_THRESHOLD -> {
                    // Qna 항목이 ITEM_COUNT_THRESHOLD 이하인 경우 마지막 Qna 항목의 하단에 radius 적용
                    holder.binding.root.setBackgroundResource(R.drawable.bg_qna_rounded_bottom)

                    // marginBottom 설정
                    val layoutParams =
                        holder.binding.root.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.bottomMargin = holder.binding.root.context.dp2Px(80)
                    holder.binding.root.layoutParams = layoutParams
                }

                else -> {
                    // 나머지 Qna 항목
                    holder.binding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.grey_03
                        )
                    ) // 배경색 설정
                }
            }
        } else if (holder is ProfileDetailViewHolder.QnaToggle) {
            // Qna 항목이 3개 이상인 경우 QnaToggle 항목의 하단에 radius를 적용
            holder.binding.root.setBackgroundResource(R.drawable.bg_qna_rounded_bottom)
        }
    }

    companion object {
        private const val ITEM_TYPE_PROFILE_INFO = 1
        private const val ITEM_TYPE_PROFILE_IMAGES = 2
        private const val ITEM_TYPE_BADGE_AND_TEEN = 3
        private const val ITEM_TYPE_INTRODUCTION = 4
        private const val ITEM_TYPE_QNA_LIST_TITLE = 5
        private const val ITEM_TYPE_QNA = 6
        private const val ITEM_TYPE_QNA_TOGGLE = 7


        private val diffUtil = object : DiffUtil.ItemCallback<ProfileDetailModel>() {
            override fun areItemsTheSame(
                oldItem: ProfileDetailModel,
                newItem: ProfileDetailModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProfileDetailModel,
                newItem: ProfileDetailModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
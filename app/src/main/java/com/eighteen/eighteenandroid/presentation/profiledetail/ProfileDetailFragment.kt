package com.eighteen.eighteenandroid.presentation.profiledetail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.AppConfig
import com.eighteen.eighteenandroid.databinding.FragmentProfileDetailBinding
import com.eighteen.eighteenandroid.domain.usecase.GetUserDetailInfoUseCase
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.media3.PlayerManager
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.showReportSelectDialogBottom
import com.eighteen.eighteenandroid.presentation.dialog.ReportDialogFragment
import com.eighteen.eighteenandroid.presentation.mediadetail.MediaDetailDialogFragment
import com.eighteen.eighteenandroid.presentation.mediadetail.model.MediaDetailMediaModel
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import com.eighteen.eighteenandroid.presentation.profiledetail.viewholder.ProfileDetailViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailFragment() :
    BaseFragment<FragmentProfileDetailBinding>(FragmentProfileDetailBinding::inflate) {

    @Inject
    lateinit var getUserDetailInfoUseCase: GetUserDetailInfoUseCase

    @Inject
    lateinit var assistedFactory: ProfileDetailViewModel.ProfileDetailAssistedFactory
    private val profileDetailViewModel by viewModels<ProfileDetailViewModel>(
        factoryProducer = {
            ProfileDetailViewModel.Factory(
                assistedFactory = assistedFactory,
                getUserDetailInfoUseCase = getUserDetailInfoUseCase,
                userId = "@Tester"
            )
        }
    )

    private var playerManager: PlayerManager? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayerManager()
        setupAdapter()
        setupRecyclerViewScrollListener()
        initViewModel()
        initFragmentResultListener()
    }

    override fun initView() {
        initNavigation()

        bind {
            ivMore.setOnClickListener {
                context?.let {
                    showReportSelectDialogBottom(
                        ivMore,
                        onReportClicked = {
                            // 신고 다이얼로그 보여주기
                            profileDetailViewModel.profileInfo?.let {
                                showDialogFragment(
                                    ReportDialogFragment.newInstance(
                                        userId = it.id,
                                        userName = it.name
                                    )
                                )
                            }
                        },
                        onBlockClicked = {}
                    )
                }
            }
            clLike.setOnClickListener {
                profileDetailViewModel.toggleLike()
            }
            ivSound.setOnClickListener {
                playerManager?.let {
                    setVolume(it.isVolumeOn.not())
                }
            }
        }
    }

    private fun setupAdapter() {
        val onPageChangeCallbackForVisibilitySoundIcon =
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val mediaItems =
                        profileDetailViewModel.items.value.data?.filterIsInstance<ProfileDetailModel.ProfileImages>()
                            ?.firstOrNull()?.mediaItems ?: return
                    if (mediaItems.isNotEmpty() && position < mediaItems.size) {
                        val mediaItem = mediaItems[position]
                        binding.ivSound.isVisible = mediaItem.isVideo
                    }
                }
            }


        bind {
            profileDetailRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            profileDetailRecyclerview.adapter =
                ProfileDetailAdapter(
                    onPageChangeCallbackForVisibilitySoundIcon,
                    onPageChangeCallbackForImagePosition = { currentPosition ->
                        profileDetailViewModel.setCurrentPosition(currentPosition)
                    }, onLikeChangeCallback = { profileDetailViewModel.toggleLike() },
                    onQnaToggleCallback = { profileDetailViewModel.toggleItems() },
                    getCurrentPosition = { profileDetailViewModel.currentPosition },
                    onClickMedia = { position, medias ->
                        openMediaDetailDialogFragment(
                            position = position,
                            mediaModels = medias.map {
                                if (it.isVideo) MediaDetailMediaModel.Video(
                                    id = it.url ?: "",
                                    mediaUrl = it.url
                                )
                                else MediaDetailMediaModel.Image(
                                    id = it.url ?: "",
                                    mediaUrl = it.url
                                )
                            }
                        )
                    },
                    playerManager = playerManager
                )
            profileDetailRecyclerview.itemAnimator = null
        }
    }

    private fun initViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileDetailViewModel.items.collectLatest { details ->
                    when (details) {
                        is ModelState.Loading -> {
                            //TODO Loading 처리
                        }

                        is ModelState.Success -> {
                            bind {
                                (profileDetailRecyclerview.adapter as? ProfileDetailAdapter)?.submitList(
                                    details.data
                                )
                            }
                        }

                        is ModelState.Error -> {
                            //TODO Error 처리
                        }

                        is ModelState.Empty -> {
                            //do nothing
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileDetailViewModel.isLike.collect { isLiked ->
                    binding.ivHeart.setImageResource(
                        if (isLiked) R.drawable.ic_full_heart else R.drawable.ic_empty_heart
                    )
                }
            }
        }

        collectInLifecycle(
            AppConfig.getSoundOptionFlow(requireContext())
                .stateIn(viewLifecycleOwner.lifecycleScope, SharingStarted.WhileSubscribed(), false)
        ) { isVolumeOn ->
            val drawableRes =
                if (isVolumeOn) R.drawable.ic_unmute else R.drawable.ic_mute
            binding.ivSound.setImageResource(drawableRes)
            playerManager?.setVolume(isVolumeOn = isVolumeOn)
        }
    }

    private fun initPlayerManager() {
        playerManager = context?.let {
            PlayerManager(viewLifecycleOwner, it)
        }
    }

    private fun setupRecyclerViewScrollListener() {
        bind {
            profileDetailRecyclerview.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val viewPagerPosition = findViewPagerPosition()
                    val viewHolder =
                        recyclerView.findViewHolderForAdapterPosition(viewPagerPosition) as? ProfileDetailViewHolder.Images

                    if (viewHolder != null) {
                        val viewPagerBottom = viewHolder.itemView.bottom
                        val headerBottom = binding.header.bottom
                        if (headerBottom <= viewPagerBottom) {
                            binding.header.setBackgroundColor(Color.TRANSPARENT)
                            binding.ivClose.setColorFilter(Color.WHITE)
                            binding.ivMore.setColorFilter(Color.WHITE)
                            binding.ivSound.setColorFilter(Color.WHITE)
                        } else {
                            binding.header.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.background_color
                                )
                            )
                            binding.ivClose.setColorFilter(Color.BLACK)
                            binding.ivMore.setColorFilter(Color.BLACK)
                            binding.ivSound.setColorFilter(Color.BLACK)
                        }
                    }
                }
            })
        }
    }

    private fun findViewPagerPosition(): Int {
        // ViewPager2가 포함된 아이템의 위치를 반환
        return profileDetailViewModel.items.value.data?.indexOfFirst { it is ProfileDetailModel.ProfileImages }
            ?: -1
    }

    private fun setVolume(isVolumeOn: Boolean) {
        val context = context ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            AppConfig.setSoundOption(context, isVolumeOn)
        }
    }

    private fun initNavigation() {
        bind {
            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            MEDIA_DETAIL_REQUEST_KEY,
            viewLifecycleOwner,
            object : MediaDetailDialogFragment.MediaDetailDialogResultListener() {
                override fun onDismiss() {
                    playerManager?.resume()
                }
            })
    }

    override fun onDestroyView() {
        playerManager = null
        super.onDestroyView()
    }

    /**
     * 프로필 상세에서 사진/영상 클릭 시 미디어 상세화면으로 이동
     */
    private fun openMediaDetailDialogFragment(
        position: Int,
        mediaModels: List<MediaDetailMediaModel>
    ) {
        showDialogFragment(
            MediaDetailDialogFragment.newInstance(
                requestKey = MEDIA_DETAIL_REQUEST_KEY,
                startPosition = position,
                mediaModels = mediaModels,
                userName = profileDetailViewModel.profileInfo?.name,
                userId = profileDetailViewModel.profileInfo?.id
            )
        )
    }

    companion object {
        private const val MEDIA_DETAIL_REQUEST_KEY = "media_detail_request_key"
    }
}

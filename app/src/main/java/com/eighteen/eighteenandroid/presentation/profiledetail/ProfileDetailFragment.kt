package com.eighteen.eighteenandroid.presentation.profiledetail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.eighteen.eighteenandroid.databinding.FragmentProfileDetailBinding
import com.eighteen.eighteenandroid.domain.usecase.GetUserDetailInfoUseCase
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.showReportSelectDialogBottom
import com.eighteen.eighteenandroid.presentation.mediadetail.MediaDetailDialogFragment
import com.eighteen.eighteenandroid.presentation.mediadetail.MediaDetailViewModel
import com.eighteen.eighteenandroid.presentation.mediadetail.model.MediaDetailMediaModel
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import com.eighteen.eighteenandroid.presentation.profiledetail.viewholder.ProfileDetailViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDetailFragment() :
    BaseFragment<FragmentProfileDetailBinding>(FragmentProfileDetailBinding::inflate) {

    @Inject
    lateinit var getUserDetailInfoUseCase: GetUserDetailInfoUseCase

    private val mediaDetailViewModel by viewModels<MediaDetailViewModel>()

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

    private lateinit var mediaItems: List<ProfileDetailModel.MediaItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecyclerViewScrollListener()
        initViewModel()
        observeMediaItems()
        initMediaDetailFlow()
        initMediaDetailFlow()
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
                            // TODO. 유저 정보 필요
//                            showDialogFragment(ReportDialogFragment.newInstance(user))
                        },
                        onBlockClicked = {}
                    )
                }
                clLike.setOnClickListener {
                    profileDetailViewModel.toggleLike()
                }
                ivSound.setOnClickListener {
                    mediaDetailViewModel.toggleVolume()
                }
            }
        }
    }

    private fun observeMediaItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileDetailViewModel.mediaItems.collectLatest { mediaItems ->
                    val hasVideo = mediaItems.any { it.isVideo }
                    Log.d("ProfileDetailFragment", "hasVideo : $hasVideo")
                    binding.ivSound.isVisible = hasVideo
                }
            }
        }
    }

    private fun setupAdapter() {
        val onPageChangeCallbackForVisibilitySoundIcon =
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val mediaItems = profileDetailViewModel.mediaItems.value
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
                    viewLifecycleOwner,
                    onPageChangeCallbackForVisibilitySoundIcon,
                    onPageChangeCallbackForImagePosition = { currentPosition ->
                        profileDetailViewModel.setCurrentPosition(currentPosition)
                    }, onLikeChangeCallback = { profileDetailViewModel.toggleLike() },
                    onQnaToggleCallback = { profileDetailViewModel.toggleItems() },
                    getCurrentPosition = { profileDetailViewModel.currentPosition }
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaDetailViewModel.mediaDetailStateFlow.collect {
                    setVolume(isVolumeOn = it.isVolumeOn)
                }
            }
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
        val drawableRes =
            if (isVolumeOn) R.drawable.ic_unmute else R.drawable.ic_mute
        binding.ivSound.setImageResource(drawableRes)
    }

    private fun initNavigation() {
        bind {
            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    //TODO pageChangeCallback position 업데이트 추가
//TODO 샘플코드 제거 미디어 모델 flow형태로 변환 후 전달
    private fun initMediaDetailFlow() = viewLifecycleOwner.lifecycleScope.launch {
        val video1 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val video2 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        val video3 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        val image1 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
        val image2 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
        val image3 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
        val testMedias = listOf(
            MediaDetailMediaModel.Video("1", video1),
            MediaDetailMediaModel.Video("2", video2),
            MediaDetailMediaModel.Image("3", image1),
            MediaDetailMediaModel.Video("4", video3),
            MediaDetailMediaModel.Image("5", image2),
            MediaDetailMediaModel.Image("6", image3),
        )
        mediaDetailViewModel.setMedias(medias = testMedias)
    }

    /**
     * 프로필 상세에서 사진/영상 클릭 시 미디어 상세화면으로 이동
     */
    private fun openMediaDetailDialogFragment() {
        showDialogFragment(MediaDetailDialogFragment())
    }
}

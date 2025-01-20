package com.eighteen.eighteenandroid.presentation.home

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.databinding.FragmentMainBinding
import com.eighteen.eighteenandroid.domain.model.Tournament
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.createChip
import com.eighteen.eighteenandroid.presentation.common.dp2Px
import com.eighteen.eighteenandroid.presentation.common.findViewHolderOrNull
import com.eighteen.eighteenandroid.presentation.common.setTagStyle
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.showReportSelectDialogLeft
import com.eighteen.eighteenandroid.presentation.common.throttleClick
import com.eighteen.eighteenandroid.presentation.dialog.ReportDialogFragment
import com.eighteen.eighteenandroid.presentation.home.adapter.MainAdapter
import com.eighteen.eighteenandroid.presentation.home.adapter.MainAdapterListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @file MainFragment.kt
 * @date 05/08/2024
 * 메인화면
 */
@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val viewModel by viewModels<MainViewModel>()
    private val myViewModel by activityViewModels<MyViewModel>()

    private lateinit var mainAdapter: MainAdapter

//    private var aboutTeenList = listOf<AboutTeen>()
//    private var tournamentList = listOf<Tournament>()

    private lateinit var mainAdapterListener: MainAdapterListener

    val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 200 }
    val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 200 }
    var isTop = true

    private var autoScrollJob: Job? = null
    private var isAutoScrolling = false
    private var isLoading = false

    // 현재 카테고리
    private var selectedChip: Chip? = null     // 칩 버튼 View
    private var category: Tag = Tag.ALL        // 카테고리 정보

    private lateinit var _pageNumList: List<Int>

    override fun initView() {
        initChipGroup()
        initMain()
    }

    private fun getCenterItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        // RecyclerView의 중앙 Y 위치 계산
        val centerY = recyclerView.height / 2

        // 현재 화면에 보이는 첫 번째 아이템과 마지막 아이템의 포지션
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        var closestPosition = -1
        var closestDistance = Int.MAX_VALUE

        // 현재 화면에 보이는 아이템들 중 중앙에 가장 가까운 아이템 찾기
        for (i in firstVisiblePosition..lastVisiblePosition) {
            val itemView = layoutManager.findViewByPosition(i)

            // 아이템 뷰의 중앙 Y 위치 계산
            val itemCenterY = (itemView!!.top + itemView.bottom) / 2

            // 아이템 뷰의 중앙 위치와 RecyclerView의 중앙 위치 간의 거리 계산
            val distance = kotlin.math.abs(itemCenterY - centerY)

            // 가장 가까운 아이템을 찾음
            if (distance < closestDistance) {
                closestDistance = distance
                closestPosition = i
            }
        }

        return closestPosition
    }

    private fun initMainAdapter() {
        initMainAdapterListener()

        mainAdapter = MainAdapter(context = requireContext(), listener = mainAdapterListener).apply {
                stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY // 현재 스크롤 위치 저장
            }

        bind {
            with(rvMain) {
                adapter = mainAdapter
                itemAnimator = null         // Item Notify animation 제거
                addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                        val itemTotalCount = recyclerView.adapter!!.itemCount-1

                        // 스크롤이 끝에 도달했는지 확인
                        if (::_pageNumList.isInitialized && !recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                            if( _pageNumList.isNotEmpty()) {
                                if(!isLoading) {
                                    val page = _pageNumList.random()
                                    viewModel.removePage(page)
                                    viewModel.requestNextPage(category, page)
                                }
                            }
                        }

                        // 스크롤이 위로 되면 (dy < 0) 버튼 숨기기, 아래로 스크롤 시( dy > 0 ) 버튼 보여주기
                        if (dy > 0 && btnScrollTop.visibility == View.GONE) {
                            btnScrollTop.startAnimation(fadeIn)
                            btnScrollTop.visibility = View.VISIBLE
                        }

                        if (!canScrollVertically(-1)) {
                            isTop = true
                        } else {
                            isTop = false
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val layoutManager = (layoutManager as? LinearLayoutManager)

//                        Log.i("MainScrollStateChanged", "findLastVisible = ${layoutManager?.findLastVisibleItemPosition().toString()}")
//                        Log.i("MainScrollStateChanged", "findFirstVisible = ${layoutManager?.findFirstVisibleItemPosition().toString()}")
//                        Log.i("MainScrollStateChanged", "findLastCompletely = ${layoutManager?.findLastCompletelyVisibleItemPosition()}")
//                        Log.i("MainScrollStateChanged", "findFirstCompletely = ${layoutManager?.findFirstCompletelyVisibleItemPosition()}")

                        val firstVisiblePosition = layoutManager?.findFirstVisibleItemPosition()
                        if (firstVisiblePosition != null) {
                            if (firstVisiblePosition > 1) {
                                mainAdapterListener.stopAutoScroll()
                            }
                        }

                        when (newState) {
                            RecyclerView.SCROLL_STATE_IDLE -> {

                                if (isTop && btnScrollTop.isVisible) {
                                    btnScrollTop.startAnimation(fadeOut)
                                    btnScrollTop.visibility = View.GONE
                                } else {
                                    if (!btnScrollTop.isVisible) {
                                        btnScrollTop.visibility = View.VISIBLE
                                        btnScrollTop.startAnimation(fadeIn)
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                })
            }

            // Top 버튼
            btnScrollTop.throttleClick(viewLifecycleOwner.lifecycleScope) {
                moveToTop()
            }
        }
    }

    private fun moveToTop() {
        binding.appbarLayout.setExpanded(true, true)
        binding.rvMain.scrollToPosition(0)
    }

    private fun initMainAdapterListener() {
        mainAdapterListener = object : MainAdapterListener {
            /**
             * 유저 클릭, 상세로 이동
             */
            override fun onUserClicks(user: User) {
                stopAutoScroll()
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentProfileDetail)   // 유저 상세로 이동
            }

            /**
             * 유저 좋아요 클릭
             */
            override fun onUserLikeClicks(likeBtn: ImageButton, user: User) {
                stopAutoScroll()
                val likeStatus = likeBtn.isSelected

                requestWithRequiredLogin {
                    // User Like API 호출
                    viewModel.requestUserLike(likeStatus.not(), likedId = user.userId)
                }
            }

            /**
             * 유저 채팅 버튼 클릭
             */
            override fun onUserChatClicks(user: User) {
                stopAutoScroll()
//                findNavController().navigate(R.id.action_fragmentMain_to_fragmentChat)
            }

            /**
             * 유저 더보기 클릭 -> 신고, 차단 다이얼로그 보여지기
             */
            override fun onUserMoreClicks(itemView: View, user: User) {
                stopAutoScroll()
                // TODO. 차단이나 신고에 의한 UserID 필요
                showReportSelectDialogLeft(
                    itemView,
                    onReportClicked = {
                        showDialogFragment(ReportDialogFragment.newInstance(user))
                    },
                    onBlockClicked = {}
                )
            }

            /**
             * About Teen 테마 선택
             */
            override fun onAboutTeenClicks(title: String) {
                val bottomNavigationView =
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)

                when(title) {
                    "Teen" -> bottomNavigationView.selectedItemId = R.id.teenMainFragment
                    "채팅" -> bottomNavigationView.selectedItemId = R.id.fragmentChat
                    "토너먼트" -> bottomNavigationView.selectedItemId = R.id.fragmentRanking
                    "나만의 Teen" -> bottomNavigationView.selectedItemId = R.id.fragmentMyProfile
                }
            }

            /**
             * 토너먼트 클릭
             */
            override fun onTournamentClicks(tournament: Tournament) {
                when (tournament) {
                    is Tournament.Exercise -> {}
                    is Tournament.Study -> {}
                }
            }

            /**
             * 토너먼트 더보기 클릭
             */
            override fun onTournamentMoreClicks() {
//                findNavController().navigate(R.id.action_fragmentMain_to_fragmentTournament)
            }

            /**
             * 이전에 보던 인기 Teen 유저로 이동
             */
            override fun scrollToPreviousUser() {
                val popularUserListViewHolder =
                    binding.rvMain.findViewHolderOrNull<MainAdapter.CommonViewHolder.PopularUserListViewHolder>()
                val rvPopularUserList = popularUserListViewHolder?.binding?.rvMainTeenPopularList

                val layoutManager = rvPopularUserList?.layoutManager as? LinearLayoutManager
                layoutManager?.scrollToPositionWithOffset(viewModel.popularUserPosition, 0)

                val targetView = layoutManager?.findViewByPosition(viewModel.popularUserPosition)
                targetView?.let {
                    val offset = rvPopularUserList.width / 2 - it.width / 2
                    layoutManager.scrollToPositionWithOffset(viewModel.popularUserPosition, offset)
                }

                stopAutoScroll()
                startAutoScroll()
            }

            /**
             * 인기 Teen 현재 보고있는 유저 위치 저장
             */
            override fun saveUserPosition(position: Int) {
                viewModel.popularUserPosition = position
            }

            override fun startAutoScroll() {
                isAutoScrolling = true
                autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
                    val smoothScroller = linearSmoothScroller(requireContext())

                    while (isAutoScrolling) {
                        delay(4500)

                        val popularUserListViewHolder =
                            binding.rvMain.findViewHolderOrNull<MainAdapter.CommonViewHolder.PopularUserListViewHolder>()

                        val rvPopularUserList =
                            popularUserListViewHolder?.binding?.rvMainTeenPopularList
                        val itemCount = rvPopularUserList?.adapter?.itemCount ?: 0

                        if (itemCount > 0) {
                            viewModel.popularUserPosition =
                                (viewModel.popularUserPosition + 1) % itemCount
                            smoothScroller.targetPosition = viewModel.popularUserPosition
                            rvPopularUserList?.layoutManager?.startSmoothScroll(smoothScroller)
                        }
                    }
                }
            }

            override fun stopAutoScroll() {
                isAutoScrolling = false
                autoScrollJob?.cancel()
            }
        }
    }

    private fun linearSmoothScroller(context: Context) = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }

        // 스크롤 속도: 80f
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 80f / displayMetrics.densityDpi
        }
    }

    private fun initMain() {
        initMainAdapter()
        initMainItemObserver()
    }

    private fun initMainItemObserver() {
        // 남은 페이지 목록
        viewModel.pageNumList.observe(viewLifecycleOwner) {
            _pageNumList = it
        }

        myViewModel.userSignEventLiveData.observe(viewLifecycleOwner) {
            getUserData(category)
        }

        collectInLifecycle(viewModel.mainItemStateFlow) { it ->
            when(it) {
                is ModelState.Loading -> {
                    isLoading = true
                    mainAdapter.addLoadingView { lastPosition ->
                        binding.rvMain.scrollToPosition(lastPosition)
                    }
                }
                is ModelState.Success -> {
                    isLoading = false
                    mainAdapter.removeLoadingView()

                    it.data?.let { mainItems ->
                        mainAdapter.updateView(mainItems)
                    }
                }
                is ModelState.Error -> {

                }
                else ->{
                    //do nothing
                }
            }
        }

        collectInLifecycle(viewModel.userLikeStateFlow) {
            when(it) {
                is ModelState.Loading -> {

                }
                is ModelState.Success -> {
                    it.data?.let { userId ->
                        mainAdapter.updateUserLikeStatus(binding.rvMain, userId, isLike = true)
                    }
                }

                is ModelState.Error -> {

                }

                else -> {
                    //do nothing
                }
            }
        }

        collectInLifecycle(viewModel.userLikeCancelStateFlow) {
            when(it) {
                is ModelState.Loading -> {

                }
                is ModelState.Success -> {
                    it.data?.let { userId ->
                        mainAdapter.updateUserLikeStatus(binding.rvMain, userId, isLike = false)
                    }
                }

                is ModelState.Error -> {

                }

                else -> {
                    //do nothing
                }
            }
        }

    }

    private fun initChipGroup() {
        for (tag in Tag.values()) {
            val chip = createChip(requireContext(), tag.strValue)
            if (tag == Tag.ALL) { // 화면 최초 진입 시 전체 태그가 클릭된 상태여야함
                chip.setTagStyle(isBlackBackground = true)
                selectedChip = chip
                category = tag
            }
            chip.setOnClickListener { _ ->
                selectedChip?.setTagStyle(isBlackBackground = false)
                chip.setTagStyle(isBlackBackground = true)
                selectedChip = chip
                category = tag        // 현재 카테고리 값 저장

                getUserData(tag)      // 현재 카테고리에 맞는 데이터 가져오기
            }
            bind {
                chipGroup.addView(chip)
            }
        }

        // 전체 유저 가져오기
        // 페이지 0부터
    }

    private fun getUserData(tag: Tag) {
        viewModel.initMain(tag)
    }
}
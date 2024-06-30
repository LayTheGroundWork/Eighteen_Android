package com.eighteen.eighteenandroid.presentation.profiledetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eighteen.eighteenandroid.databinding.FragmentProfileDetailBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.mediadetail.MediaDetailDialogFragment
import com.eighteen.eighteenandroid.presentation.mediadetail.MediaDetailViewModel
import com.eighteen.eighteenandroid.presentation.mediadetail.model.MediaDetailModel
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ProfileDetailFragment :
    BaseFragment<FragmentProfileDetailBinding>(FragmentProfileDetailBinding::inflate) {

    private val mediaDetailViewModel by viewModels<MediaDetailViewModel>()
    private val profileDetailViewModel by viewModels<ProfileDetailViewModel>()
    private lateinit var adapter: QuestionAnswerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    override fun initView() {
        initNavigation()
        setupProfileImages()
        setupQuestionAnswerList()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            profileDetailViewModel.items.collectLatest { details ->
                details.forEach { detail ->
                    when (detail) {
                        is ProfileDetailModel.ProfileInfo -> {
                            updateProfileInfo(detail)
                        }

                        is ProfileDetailModel.ProfileImages -> {
                            setupViewPagerAndTabs(detail)
                        }

                        is ProfileDetailModel.Introduction -> updateIntroduction(detail)
                        is ProfileDetailModel.Like -> updateLike(detail)
                        else -> {
                            handleUnknownDetail(detail)
                        }
                    }
                }
            }
        }
    }

    private fun updateProfileInfo(profileInfo: ProfileDetailModel.ProfileInfo) {
        bind {
            tvName.text = profileInfo.name
            tvAge.text = profileInfo.age.toString()
            tvSchool.text = profileInfo.school
        }
    }

    private fun updateIntroduction(introduction: ProfileDetailModel.Introduction) {
        bind {
            chipPersonalityType.text = introduction.personalityType
            tvIntroduction.text = introduction.introductionText
        }
    }

    private fun updateLike(like: ProfileDetailModel.Like) {
        bind { }
    }

    private fun setupViewPagerAndTabs(profileImages: ProfileDetailModel.ProfileImages) {
        bind {
            viewPager.adapter = ViewPagerAdapter(profileImages.imageUrl)
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun handleUnknownDetail(detail: ProfileDetailModel) {
        //TODO 알 수 없는 detail을 처리하는 로직
    }


    private fun setupQuestionAnswerList() {
        adapter = QuestionAnswerAdapter(viewLifecycleOwner, profileDetailViewModel)
        bind {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }

        val initialQnAList = listOf(
            ProfileDetailModel.Qna(
                question = "1. Lorem ipsum dolor sit amet?",
                answer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            ProfileDetailModel.Qna(
                question = "1. Lorem ipsum dolor sit amet?",
                answer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            ProfileDetailModel.Qna(
                question = "1. Lorem ipsum dolor sit amet?",
                answer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            ProfileDetailModel.Qna(
                question = "1. Lorem ipsum dolor sit amet?",
                answer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            ProfileDetailModel.Qna(
                question = "1. Lorem ipsum dolor sit amet?",
                answer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
            ProfileDetailModel.Qna(
                question = "1. Lorem ipsum dolor sit amet?",
                answer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ),
        )
        profileDetailViewModel.setItems(initialQnAList)
    }

    private fun setupProfileImages() {
        val profileImages = ProfileDetailModel.ProfileImages(
            imageUrl = listOf(
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
                "https://contents-cdn.viewus.co.kr/image/2023/12/CP-2022-0017/image-de4d5a79-bbe3-4c2e-84a7-f36976345663.jpeg",
                "https://cdn.hankooki.com/news/photo/202309/107376_146623_1695826504.jpg",
                "https://cdn.dailycc.net/news/photo/202312/766253_670987_1515.png",
                "https://cdn.newsculture.press/news/photo/202306/525899_650590_620.jpg",
            )
        )

        setupViewPagerAndTabs(profileImages)
        initMediaDetailFlow()

        profileDetailViewModel.setItems(listOf(profileImages))
    }

    private fun initNavigation() {
        bind {
//            btnGoMain.setOnClickListener {
//                val navController =
//                    Navigation.findNavController(requireActivity().findViewById(R.id.fragment_container_view))
//                navController.navigate(R.id.action_fragmentProfileDetail_to_fragmentMain)
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
            MediaDetailModel.Video("1", "https://picsum.photos/id/23/200/300", video1),
            MediaDetailModel.Video("2", "https://picsum.photos/id/24/200/300", video2),
            MediaDetailModel.Image("3", image1),
            MediaDetailModel.Video("4", "https://picsum.photos/id/25/200/300", video3),
            MediaDetailModel.Image("5", image2),
            MediaDetailModel.Image("6", image3),
        )
        val testFlow = flowOf(testMedias)
        mediaDetailViewModel.setMediasFlow(testFlow)
    }

    /**
     * 프로필 상세에서 사진/영상 클릭 시 미디어 상세화면으로 이동
     */
    private fun openMediaDetailDialogFragment() {
        showDialogFragment(MediaDetailDialogFragment())
    }
}




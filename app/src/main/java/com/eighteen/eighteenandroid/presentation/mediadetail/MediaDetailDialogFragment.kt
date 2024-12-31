package com.eighteen.eighteenandroid.presentation.mediadetail

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.AppConfig
import com.eighteen.eighteenandroid.common.MEDIA_COUNT
import com.eighteen.eighteenandroid.common.safeLet
import com.eighteen.eighteenandroid.databinding.FragmentMediaDetailDialogBinding
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.getParcelableListOrNull
import com.eighteen.eighteenandroid.presentation.common.media3.viewpager2.ViewPagerPlayerManager
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.showReportSelectDialogBottom
import com.eighteen.eighteenandroid.presentation.dialog.ReportDialogFragment
import com.eighteen.eighteenandroid.presentation.mediadetail.model.MediaDetailMediaModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MediaDetailDialogFragment :
    BaseDialogFragment<FragmentMediaDetailDialogBinding>(FragmentMediaDetailDialogBinding::inflate) {

    private val mediaDetailViewModel by viewModels<MediaDetailViewModel>(factoryProducer = {
        MediaDetailViewModel.Factory(startPosition = startPosition)
    })

    private val mediaPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.ivBtnSound.isVisible =
                mediaModels.getOrNull(position) is MediaDetailMediaModel.Video
            mediaDetailViewModel.selectedIndex = position
        }
    }

    private var viewPagerPlayerManager: ViewPagerPlayerManager? = null
    private val startPosition get() = arguments?.getInt(ARGUMENT_START_POSITION_KEY) ?: 0
    private val mediaModels
        get() = arguments?.getParcelableListOrNull(
            ARGUMENT_MEDIA_MODELS_KEY,
            MediaDetailMediaModel::class.java
        ) ?: emptyList()

    private val userId get() = arguments?.getString(ARGUMENT_USER_ID_KEY)
    private val userName get() = arguments?.getString(ARGUMENT_USER_NAME_KEY)
    override fun onResume() {
        super.onResume()
        initDialogWindow()
    }

    private fun initDialogWindow() {
        dialog?.window?.run {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun initView() {
        initPlayerManager()
        binding.ivBtnSound.isVisible =
            mediaModels.getOrNull(startPosition) is MediaDetailMediaModel.Video
        bind {
            ivBtnClose.setOnClickListener {
                dismiss()
            }
            ivBtnOption.setOnClickListener {
                context?.let {
                    showReportSelectDialogBottom(
                        ivBtnOption,
                        onReportClicked = {
                            safeLet(userId, userName) { userId, userName ->
                                showDialogFragment(
                                    ReportDialogFragment.newInstance(
                                        userId = userId,
                                        userName = userName
                                    )
                                )
                            }
                        },
                        onBlockClicked = {}
                    )
                }
            }
            ivBtnSound.setOnClickListener {
                viewPagerPlayerManager?.let {
                    setVolume(isVolumeOn = it.isVolumeOn.not())
                }
            }
        }
        bindMediaPager()
        initStateFlow()
    }

    private fun initPlayerManager() {
        viewPagerPlayerManager = context?.let {
            ViewPagerPlayerManager(
                viewPager2 = binding.vpMedias,
                lifecycleOwner = viewLifecycleOwner,
                context = it
            )
        }
    }

    private fun initStateFlow() {
        val context = context ?: return
        collectInLifecycle(
            AppConfig.getSoundOptionFlow(context)
                .stateIn(viewLifecycleOwner.lifecycleScope, SharingStarted.WhileSubscribed(), false)
        ) { isVolumeOn ->
            val drawableRes =
                if (isVolumeOn) R.drawable.ic_unmute else R.drawable.ic_mute
            binding.ivBtnSound.setImageResource(drawableRes)
            viewPagerPlayerManager?.setVolume(isVolumeOn = isVolumeOn)
        }
    }

    private fun bindMediaPager() {
        bind {
            with(vpMedias) {
                adapter = MediaDetailPagerAdapter().apply {
                    submitList(mediaModels) {
                        doOnLayout {
                            viewPagerPlayerManager?.playCurrent()
                        }
                    }
                }
                offscreenPageLimit = MEDIA_COUNT.coerceAtMost(5)
                setCurrentItem(mediaDetailViewModel.selectedIndex, false)
                registerOnPageChangeCallback(mediaPageChangeCallback)
            }
        }
    }

    private fun setVolume(isVolumeOn: Boolean) {
        val context = context ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            AppConfig.setSoundOption(context, isVolumeOn)
        }
    }

    override fun onDestroyView() {
        binding.vpMedias.unregisterOnPageChangeCallback(mediaPageChangeCallback)
        viewPagerPlayerManager = null
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        arguments?.getString(
            ARGUMENT_REQUEST_KEY_KEY
        )?.let {
            setFragmentResult(it, bundleOf(RESULT_DISMISS_KEY to true))
        }
        super.onDismiss(dialog)
    }

    companion object {
        private const val ARGUMENT_REQUEST_KEY_KEY = "argument_request_key_key"
        private const val ARGUMENT_START_POSITION_KEY = "argument_start_position_key"
        private const val ARGUMENT_MEDIA_MODELS_KEY = "argument_media_models_key"
        private const val ARGUMENT_USER_NAME_KEY = "argument_user_name_key"
        private const val ARGUMENT_USER_ID_KEY = "argument_user_id_key"
        private const val RESULT_DISMISS_KEY = "result_dismiss_key"
        fun newInstance(
            requestKey: String? = null,
            startPosition: Int = 0,
            mediaModels: List<MediaDetailMediaModel>,
            userId: String? = null,
            userName: String? = null
        ) = MediaDetailDialogFragment().apply {
            arguments = bundleOf(
                ARGUMENT_REQUEST_KEY_KEY to requestKey,
                ARGUMENT_START_POSITION_KEY to startPosition,
                ARGUMENT_MEDIA_MODELS_KEY to mediaModels,
                ARGUMENT_USER_ID_KEY to userId,
                ARGUMENT_USER_NAME_KEY to userName
            )
        }
    }

    abstract class MediaDetailDialogResultListener : FragmentResultListener {
        override fun onFragmentResult(requestKey: String, result: Bundle) {
            result.getBoolean(RESULT_DISMISS_KEY).takeIf { it }?.run {
                onDismiss()
            }
        }

        open fun onDismiss() {}
    }
}
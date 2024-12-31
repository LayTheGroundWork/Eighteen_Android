package com.eighteen.eighteenandroid.presentation.editmedia.video

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.view.MotionEvent
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.OptIn
import androidx.core.graphics.scale
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.AppConfig
import com.eighteen.eighteenandroid.databinding.FragmentEditVideoBinding
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.contentUriToPath
import com.eighteen.eighteenandroid.presentation.common.ffmpeg.FfmpegUtils
import com.eighteen.eighteenandroid.presentation.common.getFileExtension
import com.eighteen.eighteenandroid.presentation.common.getVideoThumbnailFromUri
import com.eighteen.eighteenandroid.presentation.common.media3.MediaInfo
import com.eighteen.eighteenandroid.presentation.common.media3.PlayerManager
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.PopUpDialogFragment
import com.eighteen.eighteenandroid.presentation.editmedia.BaseEditMediaFragment
import com.eighteen.eighteenandroid.presentation.editmedia.model.EditMediaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime


class EditVideoFragment :
    BaseEditMediaFragment<FragmentEditVideoBinding>(FragmentEditVideoBinding::inflate) {
    private var playerManager: PlayerManager? = null

    private var timelineProgressUpdateJob: Job? = null
    private var duration: Int? = null
    override fun initView() {
        initPlayerManager()
        bind {
            inHeader.ivBtnClose.setOnClickListener {
                findNavController().popBackStack()
            }
            inHeader.ivBtnCheck.setOnClickListener {
                trimVideoAndFinishIfSuccess()
            }
            inHeader.ivBtnSound.isVisible = true
            inHeader.ivBtnSound.setOnClickListener {
                playerManager?.let {
                    setVolume(isVolumeOn = it.isVolumeOn.not())
                }
            }
        }
        initStateFlow()
    }

    private fun setVolume(isVolumeOn: Boolean) {
        val context = context ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            AppConfig.setSoundOption(context, isVolumeOn)
        }
    }

    private fun initStateFlow() {
        val context = context ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editMediaViewModel.mediaUriStringStateFlow.collect {
                    setVideo(uriString = it)
                }
            }
        }
        collectInLifecycle(
            AppConfig.getSoundOptionFlow(context)
                .stateIn(viewLifecycleOwner.lifecycleScope, SharingStarted.WhileSubscribed(), false)
        ) { isVolumeOn ->
            val drawableRes =
                if (isVolumeOn) R.drawable.ic_unmute else R.drawable.ic_mute
            binding.inHeader.ivBtnSound.setImageResource(drawableRes)
            playerManager?.setVolume(isVolumeOn = isVolumeOn)
        }
    }

    private fun initPlayerManager() {
        playerManager = context?.let {
            PlayerManager(lifecycleOwner = viewLifecycleOwner, context = it)
        }
    }

    @OptIn(UnstableApi::class)
    private fun setVideo(uriString: String) {
        val mediaInfo = MediaInfo(id = uriString, mediaUrl = uriString, mediaView = binding.mvVideo)
        playerManager?.play(mediaInfo = mediaInfo)
        val mediaPlayer = MediaPlayer.create(context, Uri.parse(uriString))
        duration = mediaPlayer.duration
        mediaPlayer.release()
        initTimelineImages(uriString = uriString)
        timelineProgressUpdateJob?.cancel()
        timelineProgressUpdateJob = viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    delay(TIMELINE_UPDATE_JOB_MIL_SEC)
                    playerManager?.let {
                        if (!it.isPlaying) return@let
                        updateTimelineProgress()
                    }
                }
            }
        }
    }

    private fun initTimelineImages(uriString: String) {
        val timelineLayout = binding.clTimeline
        val context = context ?: return
        val duration = duration ?: return
        timelineLayout.doOnLayout {
            val timelineImageHeight =
                resources.getDimensionPixelOffset(R.dimen.edit_media_video_timeline_height)
            val timelineImageCount = it.width / timelineImageHeight
            with(binding.ivTimelineImages) {
                updateLayoutParams {
                    width = timelineImageCount * timelineImageHeight
                }
                val times = List(timelineImageCount) { idx ->
                    (duration * idx / timelineImageCount).toLong() * 1000L
                }
                val thumbnailBitmaps =
                    getVideoThumbnailFromUri(context = context, uri = Uri.parse(uriString), times)
                val timelineBitmap = createTimelineBitmap(
                    bitmaps = thumbnailBitmaps,
                    width = it.width,
                    height = timelineImageHeight
                )
                thumbnailBitmaps.forEach { thumbnailBitmap -> thumbnailBitmap?.recycle() }
                background = BitmapDrawable(resources, timelineBitmap)
                doOnLayout {
                    initTimelineProgress()
                    initTrimView()
                }
            }
        }
    }

    private fun createTimelineBitmap(bitmaps: List<Bitmap?>, width: Int, height: Int): Bitmap {
        val timelineBitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(timelineBitmap)
        bitmaps.forEachIndexed { idx, bitmap ->
            bitmap ?: return@forEachIndexed
            val left = (width * idx / bitmaps.size).toFloat()
            val scaledBitmap = bitmap.scale(width / bitmaps.size, height)
            canvas.drawBitmap(scaledBitmap, left, 0f, null)
        }
        return timelineBitmap
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initTimelineProgress() {
        binding.ivTimelineImages.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> binding.ivTimelineImages.performClick()
                MotionEvent.ACTION_DOWN -> {
                    playerManager?.let { playerManager ->
                        val clickedPosition =
                            playerManager.duration * motionEvent.x.toLong() / view.width
                        playerManager.seekTo(clickedPosition)
                        updateTimelineProgress()
                    }
                }
                else -> {
                    //do nothing
                }
            }
            true
        }
    }

    private fun updateTimelineProgress() {
        val (currentPosition, duration) = (playerManager ?: return).run {
            currentPosition to duration
        }
        binding.ivProgress.updateLayoutParams<MarginLayoutParams> {
            marginStart = (binding.ivTimelineImages.width * currentPosition / duration).toInt()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initTrimView() {
        bind {
            updateTrimView(0, 0, TrimViewType.START)
            ivTrimStart.setOnTouchListener(object : EditVideoTrimViewTouchListener() {
                override fun onActionMove(
                    actionDownMarginStartPx: Int,
                    actionDownMarginEndPx: Int,
                    diff: Int
                ) {
                    updateTrimView(
                        startTrimViewMargin = actionDownMarginStartPx + diff,
                        endTrimViewMargin = binding.ivTrimEnd.marginEnd,
                        actionMoveViewType = TrimViewType.START
                    )
                }

            })
            ivTrimEnd.setOnTouchListener(object : EditVideoTrimViewTouchListener() {
                override fun onActionMove(
                    actionDownMarginStartPx: Int,
                    actionDownMarginEndPx: Int,
                    diff: Int
                ) {
                    updateTrimView(
                        startTrimViewMargin = binding.ivTrimStart.marginStart,
                        endTrimViewMargin = actionDownMarginEndPx - diff,
                        actionMoveViewType = TrimViewType.END
                    )
                }
            })
        }
    }

    private fun updateTrimView(
        startTrimViewMargin: Int,
        endTrimViewMargin: Int,
        actionMoveViewType: TrimViewType
    ) {
        if (startTrimViewMargin < 0 || endTrimViewMargin < 0) return
        val timelineWidth = binding.ivTimelineImages.width.takeIf { it > 0 } ?: return
        val duration = duration ?: return
        val betweenMargin = timelineWidth - startTrimViewMargin - endTrimViewMargin
        val betweenTime = timelineWidthToTime(betweenMargin)
        bind {
            var startTrimViewMarginResult = startTrimViewMargin
            var endTrimViewMarginResult = endTrimViewMargin
            val isValidTimeRange =
                (MINIMUM_DURATION_MIL_SEC..MAXIMUM_DURATION_MIL_SEC).contains(betweenTime)
            if (duration > MINIMUM_DURATION_MIL_SEC && !isValidTimeRange) {
                if (actionMoveViewType == TrimViewType.START) {
                    startTrimViewMarginResult = startTrimViewMargin.coerceIn(
                        0..timelineWidth - timeToTimelineWidth(MINIMUM_DURATION_MIL_SEC.toInt())
                    )
                    endTrimViewMarginResult = endTrimViewMargin.coerceIn(
                        timelineWidth - startTrimViewMarginResult - timeToTimelineWidth(
                            MAXIMUM_DURATION_MIL_SEC.toInt()
                        )..timelineWidth - startTrimViewMarginResult - timeToTimelineWidth(
                            MINIMUM_DURATION_MIL_SEC.toInt()
                        )
                    )

                } else {
                    endTrimViewMarginResult = endTrimViewMargin.coerceIn(
                        0..timelineWidth - timeToTimelineWidth(
                            MINIMUM_DURATION_MIL_SEC.toInt()
                        )
                    )
                    startTrimViewMarginResult = startTrimViewMargin.coerceIn(
                        timelineWidth - endTrimViewMarginResult - timeToTimelineWidth(
                            MAXIMUM_DURATION_MIL_SEC.toInt()
                        )..timelineWidth - endTrimViewMarginResult - timeToTimelineWidth(
                            MINIMUM_DURATION_MIL_SEC.toInt()
                        )
                    )
                }
            }
            ivTrimStart.updateLayoutParams<MarginLayoutParams> {
                marginStart = startTrimViewMarginResult
            }
            ivTrimEnd.updateLayoutParams<MarginLayoutParams> {
                marginEnd = endTrimViewMarginResult
            }
        }
        updateTimeText()
    }

    private fun timeToTimelineWidth(time: Int): Int {
        val duration = duration ?: return 0
        val totalWidth = binding.ivTimelineImages.width.takeIf { it > 0 } ?: return 0
        return totalWidth * time / duration
    }

    private fun timelineWidthToTime(width: Int): Int {
        val duration = duration ?: return 0
        val totalWidth = binding.ivTimelineImages.width.takeIf { it > 0 } ?: return 0
        return width * duration / totalWidth
    }

    private fun updateTimeText() {
        bind {
            val startTime = getTrimStartTime()
            val startMin = startTime / 60
            val startSec = startTime % 60
            tvTimeStart.text = resources.getString(R.string.time_min_sec, startMin, startSec)
            val endTime = getTrimEndTime()
            val endMin = endTime / 60
            val endSec = endTime % 60
            tvTimeEnd.text = resources.getString(R.string.time_min_sec, endMin, endSec)
        }
    }

    private fun getTrimStartTime() = timelineWidthToTime(binding.ivTrimStart.marginStart) / 1000

    private fun getTrimEndTime(): Int {
        return ((duration ?: return 0) - timelineWidthToTime(binding.ivTrimEnd.marginEnd)) / 1000
    }

    @OptIn(UnstableApi::class)
    private fun trimVideoAndFinishIfSuccess() {
        val context = context ?: return
        val uri = Uri.parse(editMediaViewModel.mediaUriStringStateFlow.value)
        val path = contentUriToPath(context, uri) ?: return

        val extension = getFileExtension(context = context, uri = uri)
        val resultUrl =
            "${context.getExternalFilesDir("Video")?.path}/${LocalTime.now()}.$extension"
        binding.inLoading.root.isVisible = true
        FfmpegUtils.trimVideo(
            mediaUriString = path,
            resultMediaUriString = resultUrl,
            startTimeSec = getTrimStartTime(),
            endTimeSec = getTrimEndTime(),
            onSuccess = {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        val editResult = EditMediaResult.Video(uriString = resultUrl)
                        editMediaViewModel.setEditResultEvent(result = editResult)
                        findNavController().popBackStack()
                    }
                }
            },
            onCancel = {
                binding.inLoading.root.isVisible = false
                val dialogFragment = PopUpDialogFragment.newInstance(
                    title = getString(R.string.edit_media_cancel_title),
                    buttonText = getString(R.string.confirm)
                )
                showDialogFragment(dialogFragment)
            },
            onFailure = {
                binding.inLoading.root.isVisible = false
                val dialogFragment = PopUpDialogFragment.newInstance(
                    title = getString(R.string.edit_media_failure_title),
                    buttonText = getString(R.string.confirm)
                )
                showDialogFragment(dialogFragment)
            }
        )
    }

    override fun onDestroyView() {
        playerManager = null
        super.onDestroyView()
    }

    private enum class TrimViewType {
        START,
        END
    }

    companion object {
        private const val TIMELINE_UPDATE_JOB_MIL_SEC = 100L
        private const val MINIMUM_DURATION_MIL_SEC = 1000L
        private const val MAXIMUM_DURATION_MIL_SEC = 10000L
    }
}
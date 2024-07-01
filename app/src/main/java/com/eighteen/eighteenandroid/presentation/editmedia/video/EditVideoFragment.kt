package com.eighteen.eighteenandroid.presentation.editmedia.video

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.graphics.scale
import androidx.core.view.doOnLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentEditVideoBinding
import com.eighteen.eighteenandroid.presentation.common.getVideoThumbnailFromUri
import com.eighteen.eighteenandroid.presentation.common.media3.MediaInfo
import com.eighteen.eighteenandroid.presentation.common.media3.PlayerManager
import com.eighteen.eighteenandroid.presentation.editmedia.BaseEditMediaFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
                trimVideoAndFinish()
            }
        }
        initStateFlow()
    }

    private fun initStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editMediaViewModel.mediaUriStringStateFlow.collect {
                    setVideo(uriString = it)
                }
            }
        }
    }

    private fun initPlayerManager() {
        playerManager = context?.let {
            PlayerManager(lifecycleOwner = viewLifecycleOwner, context = it)
        }
    }

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

    private fun trimVideoAndFinish() {
        Log.d("TESTLOG", "trimVideo")
        //TODO 비디오 trim 후 viewModel에 저장 후 종료
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
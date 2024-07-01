package com.eighteen.eighteenandroid.presentation.common.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.core.view.drawToBitmap
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.eighteen.eighteenandroid.databinding.ViewImageCropBinding
import com.eighteen.eighteenandroid.presentation.common.dp2Px

class ImageCropView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewImageCropBinding.inflate(LayoutInflater.from(context))

    @Px
    private val cropViewEdgePx = context.dp2Px(CROP_VIEW_EDGE_DP)

    @Px
    private val cropViewMinSizePx = context.dp2Px(CROP_VIEW_MIN_SIZE_DP)

    val targetImageView = binding.ivTargetImage

    init {
        addView(binding.root)
        initCropAreaView()
    }

    fun getCropAreaImageBitmap(): Bitmap {
        val bitmap = binding.ivTargetImage.drawToBitmap()
        val resizedBitmap = binding.ivCropArea.run {
            Bitmap.createBitmap(bitmap, x.toInt(), y.toInt(), width, height)
        }
        bitmap.recycle()
        return resizedBitmap
    }

    fun setCropAreaViewPosition(top: Int, bottom: Int, start: Int, end: Int) {
        with(binding.ivCropArea) {
            layoutParams = (layoutParams as? MarginLayoutParams)?.apply {
                val verticalMarginsValidation = isSideMarginsValidation(
                    top,
                    bottom,
                    binding.ivTargetImage.height
                )
                val horizontalMarginsValidation = isSideMarginsValidation(
                    start,
                    end,
                    binding.ivTargetImage.width
                )
                setMargins(
                    if (horizontalMarginsValidation) start else leftMargin,
                    if (verticalMarginsValidation) top else topMargin,
                    if (horizontalMarginsValidation) end else rightMargin,
                    if (verticalMarginsValidation) bottom else bottomMargin
                )
            }
        }
    }

    private fun initCropAreaView() = with(binding.ivCropArea) {
        setOnTouchListener(object : OnTouchListener {
            private var actionDownRawPoint = Point()
            private var actionDownMargins = Margins()
            private var clickedPositions: List<CropViewClickedPosition> = emptyList()
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                view ?: return true
                when (motionEvent?.action) {
                    MotionEvent.ACTION_UP -> {
                        performClick()
                    }
                    MotionEvent.ACTION_DOWN -> {
                        actionDownRawPoint = motionEvent.run { Point(rawX.toInt(), rawY.toInt()) }
                        actionDownMargins = view.layoutParams.run {
                            Margins(
                                marginTop,
                                marginBottom,
                                marginStart,
                                marginEnd
                            )
                        }
                        clickedPositions =
                            getClickedPositions(
                                motionEvent.run { Point(x.toInt(), y.toInt()) },
                                view
                            )
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val diffX = motionEvent.rawX.toInt() - actionDownRawPoint.x
                        val diffY = motionEvent.rawY.toInt() - actionDownRawPoint.y
                        updateMarginsCropArea(
                            diff = Point(diffX, diffY),
                            actionDownMargins = actionDownMargins,
                            clickedPositions = clickedPositions
                        )
                    }
                }
                return true
            }
        })
    }

    private fun getClickedPositions(
        actionDownPoint: Point,
        cropView: View
    ): List<CropViewClickedPosition> {
        val isClickedTopEdge = (0..cropViewEdgePx).contains(actionDownPoint.y)
        val isClickedBottomEdge =
            (cropView.height - cropViewEdgePx..cropView.height).contains(actionDownPoint.y)
        val isClickedStartEdge = (0..cropViewEdgePx).contains(actionDownPoint.x)
        val isClickedEndEdge =
            (cropView.width - cropViewEdgePx..cropView.width).contains(actionDownPoint.x)
        return mutableListOf<CropViewClickedPosition>().apply {
            if (isClickedTopEdge) add(CropViewClickedPosition.TOP_EDGE)
            if (isClickedBottomEdge) add(CropViewClickedPosition.BOTTOM_EDGE)
            if (isClickedStartEdge) add(CropViewClickedPosition.START_EDGE)
            if (isClickedEndEdge) add(CropViewClickedPosition.END_EDGE)
            if (this.isEmpty()) add(CropViewClickedPosition.CENTER)
        }
    }

    private fun updateMarginsCropArea(
        diff: Point,
        actionDownMargins: Margins,
        clickedPositions: List<CropViewClickedPosition>
    ) {
        fun calculateMargin(
            baseMargin: Int,
            edgePosition: CropViewClickedPosition,
            diff: Int
        ) = baseMargin + if (clickedPositions.contains(edgePosition)
            || clickedPositions.contains(CropViewClickedPosition.CENTER)
        ) diff
        else 0

        val updatedTopMargin =
            calculateMargin(actionDownMargins.top, CropViewClickedPosition.TOP_EDGE, diff.y)
        val updatedBottomMargin =
            calculateMargin(actionDownMargins.bottom, CropViewClickedPosition.BOTTOM_EDGE, -diff.y)
        val updatedStartMargin =
            calculateMargin(actionDownMargins.start, CropViewClickedPosition.START_EDGE, diff.x)
        val updatedEndMargin =
            calculateMargin(actionDownMargins.end, CropViewClickedPosition.END_EDGE, -diff.x)
        setCropAreaViewPosition(
            updatedTopMargin,
            updatedBottomMargin,
            updatedStartMargin,
            updatedEndMargin
        )
    }

    private fun isSideMarginsValidation(margin: Int, other: Int, maxSize: Int) =
        margin >= 0 && other >= 0 && margin + other + cropViewMinSizePx <= maxSize


    private data class Margins(
        val top: Int = 0,
        val bottom: Int = 0,
        val start: Int = 0,
        val end: Int = 0
    )

    private enum class CropViewClickedPosition {
        TOP_EDGE,
        BOTTOM_EDGE,
        START_EDGE,
        END_EDGE,
        CENTER
    }

    companion object {
        //TODO 크롭 뷰 선택영역, 최소사이즈 디자인 적용
        private const val CROP_VIEW_EDGE_DP = 20
        private const val CROP_VIEW_MIN_SIZE_DP = 100
    }
}
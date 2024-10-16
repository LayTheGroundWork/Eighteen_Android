package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.eighteen.eighteenandroid.presentation.common.dp2Px

class SignUpMediasItemDecoration : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val betweenMarginPx = parent.context.dp2Px(BETWEEN_MARGIN_DP)
        outRect.left = if (itemPosition == 0) betweenMarginPx else 0
        outRect.right = betweenMarginPx
    }

    companion object {
        private const val BETWEEN_MARGIN_DP = 16
    }
}
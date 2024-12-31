package com.eighteen.eighteenandroid.presentation.profiledetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader
import com.eighteen.eighteenandroid.presentation.common.media3.MediaInfo
import com.eighteen.eighteenandroid.presentation.common.media3.MediaView
import com.eighteen.eighteenandroid.presentation.common.media3.viewpager2.ViewPagerMediaItem
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel

class ViewPagerAdapter(
    private val items: List<ProfileDetailModel.MediaItem>,
    private val onClickItem: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId =
            if (viewType == ITEM_TYPE_VIDEO) R.layout.item_profile_detail_vedio else R.layout.item_profile_detail_image
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return when (viewType) {
            ITEM_TYPE_VIDEO -> {
                VideoViewHolder(view)
            }

            else -> {
                ImageViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items.getOrNull(position)

        return if (item?.isVideo == true) {
            ITEM_TYPE_VIDEO
        } else ITEM_TYPE_IMAGE
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is ImageViewHolder) holder.bind(
            mediaItem = item,
            onClick = { onClickItem(position) })
        else if (holder is VideoViewHolder) holder.bind(
            mediaItem = item,
            onClick = { onClickItem(position) })
    }

    class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ImageView)

        fun bind(mediaItem: ProfileDetailModel.MediaItem, onClick: () -> Unit) {
            ImageLoader.get().loadUrl(imageView, mediaItem.url)
            itemView.setOnClickListener {
                onClick()
            }
        }
    }

    class VideoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), ViewPagerMediaItem {
        private val mediaView: MediaView = itemView.findViewById(R.id.media_view)
        private var mediaItem: ProfileDetailModel.MediaItem? = null

        fun bind(mediaItem: ProfileDetailModel.MediaItem, onClick: () -> Unit) {
            this.mediaItem = mediaItem
            mediaView.setThumbnailUrl(mediaItem.url)
            itemView.findViewById<View>(R.id.vTouchArea).apply {
                setOnClickListener {
                    onClick()
                }
                bringToFront()
            }
        }

        @OptIn(UnstableApi::class)
        override fun getMediaInfo() =
            MediaInfo(
                id = mediaItem?.url,
                mediaUrl = mediaItem?.url ?: "",
                mediaView = mediaView,
                resizeMode = RESIZE_MODE_ZOOM
            )
    }


    companion object {
        private const val ITEM_TYPE_VIDEO = 1
        private const val ITEM_TYPE_IMAGE = 0
    }
}
package com.eighteen.eighteenandroid.presentation.common.media3

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.AttrRes
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.LayoutMediaViewBinding
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader

@OptIn(UnstableApi::class)
class MediaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutMediaViewBinding.inflate(LayoutInflater.from(context))

    val playerView = binding.pvMedia

    init {
        addView(binding.root)
        val bufferingProgressBar =
            binding.pvMedia.findViewById<ProgressBar>(androidx.media3.ui.R.id.exo_buffering)
        val color = ContextCompat.getColor(context, R.color.white)
        bufferingProgressBar.indeterminateTintList = ColorStateList.valueOf(color)
    }

    fun setPlayer(player: Player?) {
        binding.pvMedia.player = player
    }

    fun setThumbnailUrl(url: String?) {
        val artworkView = playerView.findViewById<ImageView>(androidx.media3.ui.R.id.exo_artwork)
        ImageLoader.get().loadUrl(artworkView, url)
    }
}
package com.eighteen.eighteenandroid.presentation.common.media3

import android.content.Context
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

/**
 * exoPlayer에서 실행되는 메소드들을 모아놓은 wrapper 클래스
 * @param lifecycleOwner : destroy 시점에 player를 release 하기 위해 지정하는 lifecycle
 * @param context : ExoPlayer Builder에서 사용하는 context
 */
//TODO 영상 주소 잘못됐거나 로딩 실패했을 경우 처리, 사운드 처리
open class PlayerManager(
    private val lifecycleOwner: LifecycleOwner,
    context: Context
) : DefaultLifecycleObserver {

    protected open val player = ExoPlayer.Builder(context).build()
    private val playingInfoMap = HashMap<String, PlayingInfo>()
    protected var targetMediaInfo: MediaInfo? = null

    val currentPosition get() = player.currentPosition
    val duration get() = player.duration
    val isPlaying get() = player.isPlaying

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    targetMediaInfo?.mediaView?.thumbnailView?.isVisible = false
                }
                Player.STATE_BUFFERING -> {
                    //TODO 버퍼링 중 로딩아이콘 논의 필요
                }
                Player.STATE_ENDED -> {}
                Player.STATE_IDLE -> {}
            }
        }
    }

    init {
        initLifecycle()
        initPlayer()
    }

    private fun initLifecycle() {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private fun initPlayer() {
        player.addListener(playerListener)
    }

    override fun onResume(owner: LifecycleOwner) {
        player.play()
    }

    override fun onPause(owner: LifecycleOwner) {
        player.pause()
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    open fun play(mediaInfo: MediaInfo) {
        if (mediaInfo.id == targetMediaInfo?.id) {
            player.play()
            return
        }
        targetMediaInfo?.takeIf { it != mediaInfo }?.let { targetMediaInfo ->
            detachPlayer(targetMediaInfo)
        }
        targetMediaInfo = mediaInfo
        player.setMediaItem(MediaItem.fromUri(mediaInfo.mediaUrl))
        mediaInfo.id?.let { playingInfoMap[it] }?.let { playingInfo ->
            setPlayingInfo(playingInfo)
        }
        player.prepare()
        player.play()
        mediaInfo.mediaView.setPlayer(player)
    }

    open fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    //기존에 있던 Player와 분리하고 id값이 있을 경우 재생정보 저장
    protected fun detachPlayer(mediaInfo: MediaInfo) {
        mediaInfo.id?.let {
            playingInfoMap[mediaInfo.id] = PlayingInfo(positionMs = player.currentPosition)
        }
        player.pause()
        mediaInfo.mediaView.setPlayer(null)
    }

    private fun setPlayingInfo(playingInfo: PlayingInfo) {
        player.seekTo(playingInfo.positionMs)
    }

    fun clearPlayingInfo() {
        playingInfoMap.clear()
    }

    fun removePlayingInfo(key: String) {
        playingInfoMap.remove(key)
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    private fun release() {
        targetMediaInfo = null
        player.removeListener(playerListener)
        player.release()
    }

    protected data class PlayingInfo(
        val positionMs: Long = 0L
    )
}
package com.eighteen.eighteenandroid.presentation.common.ffmpeg

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode

object FfmpegUtils {

    //TODO fps, size 논의 필요
    fun trimVideo(
        mediaUriString: String,
        resultMediaUriString: String,
        startTimeSec: Int,
        endTimeSec: Int,
        onSuccess: () -> Unit = {},
        onCancel: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val startTimeStr = getHourMinSecString(startTimeSec)
        val endTimeStr = getHourMinSecString(endTimeSec)
        FFmpegKit.executeAsync(
            "-ss $startTimeStr -to $endTimeStr -i $mediaUriString $resultMediaUriString"
        ) {
            if (ReturnCode.isSuccess(it.returnCode)) {
                onSuccess.invoke()
            } else if (ReturnCode.isCancel(it.returnCode)) {
                onCancel.invoke()
            } else {
                Log.d(
                    "FfmpegUtils",
                    it.run { "Command failed with state $state and rc $returnCode.$failStackTrace" },
                )
                onFailure.invoke()
            }
        }
    }

    private fun getHourMinSecString(timeSec: Int): String {
        val hour = timeSec / 3600
        val min = timeSec / 60
        val sec = timeSec % 60
        return String.format("%02d:%02d:%02d", hour, min, sec)
    }
}

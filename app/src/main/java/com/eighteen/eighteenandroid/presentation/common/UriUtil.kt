package com.eighteen.eighteenandroid.presentation.common

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.util.Locale

//https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android/31691791#31691791
fun getMimeTypeFromUri(uri: Uri, context: Context): String? =
    if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr: ContentResolver = context.contentResolver
        cr.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
            uri.toString()
        )
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }

fun getVideoThumbnailFromUri(context: Context, uri: Uri, time: Long): Bitmap? =
    getVideoThumbnailFromUri(context = context, uri = uri, times = listOf(time)).firstOrNull()


fun getVideoThumbnailFromUri(context: Context, uri: Uri, times: List<Long>): List<Bitmap?> {
    val retriever = MediaMetadataRetriever()
    val thumbnails = mutableListOf<Bitmap?>()
    try {
        retriever.setDataSource(context, uri)
        times.forEach { time ->
            val thumbnail = retriever.getFrameAtTime(time)
            thumbnails.add(thumbnail)
        }
    } catch (e: Throwable) {
        Log.e("getVideoThumbnailFromUri", "${e.message}")
    } finally {
        retriever.release()
    }
    return thumbnails
}
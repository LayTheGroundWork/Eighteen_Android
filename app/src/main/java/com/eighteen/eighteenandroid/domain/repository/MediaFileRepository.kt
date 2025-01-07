package com.eighteen.eighteenandroid.domain.repository

import com.eighteen.eighteenandroid.domain.model.MediaFile

interface MediaFileRepository {
    suspend fun putMediaFiles(uniqueId: String, mediaFiles: List<MediaFile>): Result<List<String>>
}
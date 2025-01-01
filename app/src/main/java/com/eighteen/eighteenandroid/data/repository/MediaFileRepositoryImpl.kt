package com.eighteen.eighteenandroid.data.repository

import com.eighteen.eighteenandroid.data.datasource.remote.service.MediaFileService
import com.eighteen.eighteenandroid.data.datasource.remote.service.S3Service
import com.eighteen.eighteenandroid.domain.model.MediaFile
import com.eighteen.eighteenandroid.domain.repository.MediaFileRepository
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class MediaFileRepositoryImpl @Inject constructor(
    private val mediaFileService: MediaFileService,
    private val s3Service: S3Service
) : MediaFileRepository {
    override suspend fun putMediaFiles(
        uniqueId: String,
        mediaFiles: List<MediaFile>
    ): Result<List<String>> {
        mediaFiles.map { it.file.asRequestBody(it.type) }
        //TODO 미디어 파일 업로드 구현
        return Result.success(emptyList())
    }
}
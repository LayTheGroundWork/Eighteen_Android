package com.eighteen.eighteenandroid.data.repository

import com.eighteen.eighteenandroid.data.datasource.remote.service.MediaFileService
import com.eighteen.eighteenandroid.data.datasource.remote.service.S3Service
import com.eighteen.eighteenandroid.data.mapper.ApiException
import com.eighteen.eighteenandroid.data.mapper.findApiExceptionByCode
import com.eighteen.eighteenandroid.domain.model.MediaFile
import com.eighteen.eighteenandroid.domain.model.MediaType
import com.eighteen.eighteenandroid.domain.repository.MediaFileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Date
import javax.inject.Inject

class MediaFileRepositoryImpl @Inject constructor(
    private val mediaFileService: MediaFileService,
    private val s3Service: S3Service
) : MediaFileRepository {
    /**
     * 1. 서버에 PreSigned Url 요청
     * 2. 해당 url을 이용하여 s3서버에 업로드
     * 3. 업로드 성공 시 key들을 반환, 실패 시 삭제요청
     */
    override suspend fun putMediaFiles(
        uniqueId: String,
        mediaFiles: List<MediaFile>
    ): Result<List<String>> {
        val postMediaResult = mediaFileService.postMediaUpload(
            uniqueId = uniqueId,
            fileNames = mediaFiles.mapIndexed { index, mediaFile -> mediaFile.createFileName(index = index) })
        if (postMediaResult.isSuccessful.not()) return Result.failure(
            findApiExceptionByCode(
                postMediaResult.code()
            )
        )
        val postMediaData = postMediaResult.body()?.data ?: emptyList()
        val putResults = coroutineScope {
            postMediaData.mapIndexed { idx, data ->
                async {
                    s3Service.putMediaFile(
                        url = data[0],
                        requestBody = mediaFiles[idx].toRequestBody()
                    )
                }
            }.awaitAll()
        }
        if (putResults.any { it.isSuccessful.not() }) {
            putResults.forEachIndexed { index, response ->
                if (response.isSuccessful) return@forEachIndexed
                mediaFileService.deleteMedia(key = postMediaData[index][1])
            }
            val exception = putResults.firstOrNull { it.isSuccessful.not() }?.code()?.let {
                findApiExceptionByCode(it)
            } ?: ApiException.Unknown
            return Result.failure(exception)
        }
        return Result.success(postMediaData.map { it[1] })
    }

    private fun MediaFile.toRequestBody() = when (this) {
        is MediaFile.FileMedia -> this.file.asRequestBody(this.mediaType.toContentType())
        is MediaFile.ByteArrayMedia -> this.byteArray.toRequestBody(this.mediaType.toContentType())
    }

    private fun MediaType.toContentType() = when (this) {
        MediaType.VIDEO -> "video/*".toMediaTypeOrNull()
        MediaType.IMAGE -> "image/*".toMediaTypeOrNull()
    }

    private fun MediaFile.createFileName(index: Int) = when (this) {
        is MediaFile.FileMedia -> this.file.name
        is MediaFile.ByteArrayMedia -> {
            if (this.mediaType == MediaType.IMAGE) "Eighteen_Image_${Date()}_$index.png"
            else "Eighteen_Image_${Date()}_$index.mp4"
        }
    }
}
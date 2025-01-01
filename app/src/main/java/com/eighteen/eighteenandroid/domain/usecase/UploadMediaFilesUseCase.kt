package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.model.MediaFile
import com.eighteen.eighteenandroid.domain.repository.MediaFileRepository
import javax.inject.Inject

class UploadMediaFilesUseCase @Inject constructor(private val repository: MediaFileRepository) {
    suspend operator fun invoke(uniqueId: String, mediaFiles: List<MediaFile>) =
        repository.putMediaFiles(uniqueId = uniqueId, mediaFiles = mediaFiles)
}
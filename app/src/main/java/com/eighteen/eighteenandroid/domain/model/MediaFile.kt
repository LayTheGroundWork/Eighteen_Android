package com.eighteen.eighteenandroid.domain.model

import java.io.File

sealed interface MediaFile {
    val mediaType: MediaType

    data class ByteArrayMedia(val byteArray: ByteArray, override val mediaType: MediaType) :
        MediaFile {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ByteArrayMedia

            if (!byteArray.contentEquals(other.byteArray)) return false
            if (mediaType != other.mediaType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = byteArray.contentHashCode()
            result = 31 * result + mediaType.hashCode()
            return result
        }
    }

    data class FileMedia(val file: File, override val mediaType: MediaType) : MediaFile

}
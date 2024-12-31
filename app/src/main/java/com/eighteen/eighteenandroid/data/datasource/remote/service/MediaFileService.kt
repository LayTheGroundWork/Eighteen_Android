package com.eighteen.eighteenandroid.data.datasource.remote.service

import com.eighteen.eighteenandroid.data.datasource.remote.response.ApiResult
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MediaFileService {
    @POST("/v1/api/file/upload")
    suspend fun postMediaUpload(
        @Query("uniqueId") uniqueId: String,
        @Query("fileNames") fileNames: List<String>
    ): Response<ApiResult<List<List<String>>>>

    @GET("/v1/api/thumbnail/view")
    suspend fun getMediaThumbnail(@Query("key") key: String): Response<ApiResult<String>>

    @GET("/v1/api/file/view")
    suspend fun getMediaFile(@Query("key") key: String): Response<ApiResult<String>>

    @GET("/v1/api/file/view-all")
    suspend fun getAllMediaFiles(): Response<ApiResult<List<String>>>

    @DELETE("/v1/api/file/delete")
    suspend fun deleteMedia(@Query("key") key: String): Response<ApiResult<List<String>>>
}
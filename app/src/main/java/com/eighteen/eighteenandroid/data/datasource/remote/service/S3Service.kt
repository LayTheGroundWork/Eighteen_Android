package com.eighteen.eighteenandroid.data.datasource.remote.service

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface S3Service {
    @PUT
    suspend fun putMediaFile(@Url url: String, @Body file: RequestBody): Response<Unit>
}
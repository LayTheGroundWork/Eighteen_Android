package com.eighteen.eighteenandroid.data.datasource.remote.service

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

//추후 모듈화 시 다른 계층에서 참조하지 않을 예정이기 때문에 internal 붙여야 함
interface S3Service {
    @PUT
    suspend fun putMediaFile(@Url url: String, @Body requestBody: RequestBody): Response<Unit>
}
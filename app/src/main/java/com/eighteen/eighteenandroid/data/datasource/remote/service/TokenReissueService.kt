package com.eighteen.eighteenandroid.data.datasource.remote.service

import retrofit2.Response
import retrofit2.http.POST

interface TokenReissueService {
    @POST("/v1/api/user/reissue")
    suspend fun postTokenReissue(): Response<Unit>
}
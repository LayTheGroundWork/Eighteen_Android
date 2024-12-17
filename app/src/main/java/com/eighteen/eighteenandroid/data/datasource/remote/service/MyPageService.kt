package com.eighteen.eighteenandroid.data.datasource.remote.service

import com.eighteen.eighteenandroid.data.datasource.remote.request.MyProfileRequest
import com.eighteen.eighteenandroid.data.datasource.remote.response.ApiResult
import com.eighteen.eighteenandroid.data.datasource.remote.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MyPageService {
    //TODO header 적용
    @POST("/v1/api/my-page/update")
    suspend fun postMyPageUpdate(@Body myProfileRequest: MyProfileRequest): Response<ApiResult<String?>>

    @GET("/v1/api/my-page")
    suspend fun getMyPage(): Response<ApiResult<ProfileResponse?>>
}
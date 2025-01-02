package com.eighteen.eighteenandroid.data.datasource.remote.service

import com.eighteen.eighteenandroid.data.datasource.remote.request.SignUpRequest
import com.eighteen.eighteenandroid.data.datasource.remote.response.ApiResult
import com.eighteen.eighteenandroid.data.datasource.remote.response.ProfileDetailResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("/api/v1/user/profile")
    suspend fun getUserInfo(): Response<List<UserResponse>>

    @POST("/v1/api/user/sign-up")
    suspend fun postSignUp(
        @Query("profileImageKeys") profileImageKeys: List<String>,
        @Body signUpRequest: SignUpRequest
    ): Response<ApiResult<String>>

    @POST("/v1/api/user/find/{uniqueId}")
    suspend fun postProfileDetailInfo(@Path("uniqueId") uniqueId: String): Response<ApiResult<ProfileDetailResponse>>

    @POST("/v1/api/user/duplication-check")
    suspend fun postDuplicationCheck(@Query("uniqueId") uniqueId: String): Response<ApiResult<Boolean>>

    @POST("/v1/api/user/sign-in")
    suspend fun postLogin(@Query("phoneNumber") phoneNumber: String): Response<ApiResult<String>>
}
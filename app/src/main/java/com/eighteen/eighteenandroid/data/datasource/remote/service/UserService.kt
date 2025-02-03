package com.eighteen.eighteenandroid.data.datasource.remote.service

import com.eighteen.eighteenandroid.data.datasource.remote.request.SignUpRequest
import com.eighteen.eighteenandroid.data.datasource.remote.response.ApiResult
import com.eighteen.eighteenandroid.data.datasource.remote.response.ProfileDetailResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.UserDto
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

    @GET("/v1/api/{userType}/find-all/{category}")
    suspend fun getCategoryUser(@Path("userType") userType: String, @Path("category") category: String, @Query("page") page: Int, @Query("size") size: Int = 10): Response<ApiResult<UserResponse>>

    @POST("/v1/api/user/like")
    suspend fun postLikeUser(@Query("likedId") likedId: Int): Response<ApiResult<String>>

    @POST("/v1/api/user/like-cancel")
    suspend fun postLikeCancelUser(@Query("likedId") likedId: Int): Response<ApiResult<String>>

    @GET("/v1/api/teen/{userType}/famous/{category}")
    suspend fun getPopularUser(@Path("userType") userType: String, @Path("category") category: String): Response<ApiResult<List<UserDto>>>
}
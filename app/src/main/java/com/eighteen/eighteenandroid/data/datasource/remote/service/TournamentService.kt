package com.eighteen.eighteenandroid.data.datasource.remote.service

import com.eighteen.eighteenandroid.data.datasource.remote.request.TournamentRequest
import com.eighteen.eighteenandroid.data.datasource.remote.response.ApiResult
import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentCategoryResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentThisWeekParticipantsResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentVoteResultResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TournamentService {
    @GET("/v1/api/tournament/search")
    suspend fun getTournamentParticipants(): Response<ApiResult<List<TournamentCategoryResponse>>>

    @GET("/v1/api/tournament/participant/{category}/this-week")
    suspend fun getThisWeekParticipants(
        @Path("category") category: String
    ): Response<ApiResult<List<TournamentThisWeekParticipantsResponse>>>

    @POST("/v1/api/tournament/final/vote")
    suspend fun submitTournamentResults(
        @Body tournamentRequest: TournamentRequest
    ): Response<ApiResult<String>>

    @GET("/v1/api/tournament/final/result")
    suspend fun getTournamentResult(@Query("tournamentNo") tournamentNo: Int): Response<ApiResult<List<TournamentVoteResultResponse>>>
}
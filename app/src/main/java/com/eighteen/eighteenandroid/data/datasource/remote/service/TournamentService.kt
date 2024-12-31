package com.eighteen.eighteenandroid.data.datasource.remote.service

import com.eighteen.eighteenandroid.data.datasource.remote.response.ApiResult
import com.eighteen.eighteenandroid.data.datasource.remote.response.TournamentVoteResultResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TournamentService {
    @GET("/v1/api/tournament/final/result")
    suspend fun getTournamentResult(@Query("tournamentNo") tournamentNo: Int): Response<ApiResult<List<TournamentVoteResultResponse>>>
}
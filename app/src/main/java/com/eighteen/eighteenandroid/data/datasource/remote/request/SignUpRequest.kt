package com.eighteen.eighteenandroid.data.datasource.remote.request

//TODO tournamentJoin 필드 확인 필요
data class SignUpRequest(
    val phoneNumber: String,
    val uniqueId: String,
    val nickName: String,
    val birthDay: String,
    val schoolData: SchoolRequest,
    val category: String,
    val tournamentJoin: Boolean = true
)

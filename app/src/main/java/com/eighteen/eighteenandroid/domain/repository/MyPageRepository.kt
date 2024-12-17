package com.eighteen.eighteenandroid.domain.repository

import com.eighteen.eighteenandroid.domain.model.Mbti
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.model.Qna
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.domain.model.SnsInfo

interface MyPageRepository {
    suspend fun postMyPageUpdate(
        accessToken: String,
        nickName: String?,
        school: School?,
        snsInfo: List<SnsInfo>,
        mbti: Mbti?,
        introduction: String?,
        questions: List<Qna>
    ): Result<String?>

    suspend fun getMyPageProfile(): Result<Profile?>
}
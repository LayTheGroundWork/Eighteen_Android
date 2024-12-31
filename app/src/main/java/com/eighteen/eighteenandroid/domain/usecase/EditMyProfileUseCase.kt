package com.eighteen.eighteenandroid.domain.usecase

import com.eighteen.eighteenandroid.domain.model.Mbti
import com.eighteen.eighteenandroid.domain.model.Qna
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.domain.model.SnsInfo
import com.eighteen.eighteenandroid.domain.repository.MyPageRepository
import javax.inject.Inject

class EditMyProfileUseCase @Inject constructor(private val repository: MyPageRepository) {
    suspend operator fun invoke(
        nickName: String?,
        school: School?,
        snsInfo: List<SnsInfo>,
        mbti: Mbti?,
        introduction: String?,
        questions: List<Qna>
    ) = repository.postMyPageUpdate(
        nickName = nickName,
        school = school,
        snsInfo = snsInfo,
        mbti = mbti,
        introduction = introduction,
        questions = questions
    )
}
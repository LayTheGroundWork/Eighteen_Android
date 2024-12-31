package com.eighteen.eighteenandroid.data.repository

import com.eighteen.eighteenandroid.data.datasource.remote.request.MyProfileRequest
import com.eighteen.eighteenandroid.data.datasource.remote.response.QuestionResponse
import com.eighteen.eighteenandroid.data.datasource.remote.response.SnsPlatformResponse
import com.eighteen.eighteenandroid.data.datasource.remote.service.MyPageService
import com.eighteen.eighteenandroid.data.mapper.MyPageMapper.toProfile
import com.eighteen.eighteenandroid.data.mapper.mapper
import com.eighteen.eighteenandroid.domain.model.Mbti
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.model.Qna
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.domain.model.SnsInfo
import com.eighteen.eighteenandroid.domain.repository.MyPageRepository
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(private val myPageService: MyPageService) :
    MyPageRepository {
    override suspend fun postMyPageUpdate(
        nickName: String?,
        school: School?,
        snsInfo: List<SnsInfo>,
        mbti: Mbti?,
        introduction: String?,
        questions: List<Qna>
    ): Result<String?> = runCatching {
        val myProfileRequest = MyProfileRequest(
            nickName = nickName,
            schoolData = school?.let {
                MyProfileRequest.SchoolData(schoolName = it.name, schoolLocation = it.address)
            },
            snsPlatform = SnsPlatformResponse(
                instagram = snsInfo.find { it.type == SnsInfo.SnsType.INSTAGRAM }?.id,
                x = snsInfo.find { it.type == SnsInfo.SnsType.X }?.id,
                tiktok = snsInfo.find { it.type == SnsInfo.SnsType.TIKTOK }?.id,
                youtube = snsInfo.find { it.type == SnsInfo.SnsType.YOUTUBE }?.id
            ),
            mbti = mbti?.mbtiString,
            introduction = introduction,
            //TODO 질문 이름 적용
            questions = questions.map {
                QuestionResponse(
                    question = it.question.name,
                    answer = it.answer
                )
            }
        )
        //TODO 응답 값 확인
        myPageService.postMyPageUpdate(myProfileRequest).mapper {
            ""
        }
    }

    override suspend fun getMyPageProfile(): Result<Profile?> = runCatching {
        myPageService.getMyPage().mapper {
            it.data?.toProfile()
        }
    }
}
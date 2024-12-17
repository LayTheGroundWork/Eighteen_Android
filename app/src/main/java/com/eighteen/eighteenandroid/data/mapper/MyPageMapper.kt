package com.eighteen.eighteenandroid.data.mapper

import com.eighteen.eighteenandroid.data.datasource.remote.response.ProfileResponse
import com.eighteen.eighteenandroid.data.mapper.QnaMapper.toQna
import com.eighteen.eighteenandroid.data.mapper.SnsPlatformMapper.toSnsInfoList
import com.eighteen.eighteenandroid.domain.model.Media
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.domain.model.createMbtiOrNull
import java.util.Date

object MyPageMapper {
    //TODO 뱃지, 마지막 우승 추가, 미디어 분기 추가, id int형으로 변경, QNA 매핑 추가, 나이
    fun ProfileResponse.toProfile() = Profile(
        nickName = nickName ?: "",
        age = 0,
        birth = Date(),
        school = School(name = schoolName ?: "", address = location ?: ""),
        badgeCount = 0,
        teenDescription = "",
        medias = profileImages?.map { Media.Image(url = it) } ?: emptyList(),
        id = id.toString(),
        snsInfoList = snsPlatform?.toSnsInfoList() ?: emptyList(),
        mbti = createMbtiOrNull(mbti ?: ""),
        introduction = introduction,
        qna = questions?.mapNotNull { it.toQna() } ?: emptyList(),
        description = ""
    )
}
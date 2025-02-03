package com.eighteen.eighteenandroid.data.mapper

import com.eighteen.eighteenandroid.data.datasource.remote.response.UserDto
import com.eighteen.eighteenandroid.data.datasource.remote.response.UserResponse
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.domain.model.UserUseCaseModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

object UserMapper {
    fun UserDto.toUser(): User {
        fun calculateAge(birthDay: String): Int {
            // 날짜 포맷터 생성
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            try {
                // 문자열을 LocalDate 객체로 변환
                val birthDate = LocalDate.parse(birthDay, formatter)

                // 현재 날짜 가져오기
                val currentDate = LocalDate.now()

                // Period를 사용하여 두 날짜 사이의 기간 계산
                val period = Period.between(birthDate, currentDate)

                // 만 나이 계산
                return period.years
            } catch (e: Exception) {
                throw IllegalArgumentException("올바른 날짜 형식이 아닙니다. 'yyyy-MM-dd' 형식으로 입력해주세요.")
            }
        }

        return User(
            userId = id,
            userImage = profileImage?: "",
            uniqueId = uniqueId,
            userName = nickName,
            userAge = calculateAge(birthDay).toString(),
            userSchoolName = schoolName,
            likeStatus = likeStatus
        )
    }

    fun UserResponse.toUserUseCaseModel() = UserUseCaseModel(
        users = users.map { it.toUser() },
        totalPageCount = totalPage
    )
}
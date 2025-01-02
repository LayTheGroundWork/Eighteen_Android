package com.eighteen.eighteenandroid.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.eighteen.eighteenandroid.common.safeLet
import com.eighteen.eighteenandroid.data.ACCESS_TOKEN_PREFERENCE_KEY
import com.eighteen.eighteenandroid.data.HEADER_AUTHORIZATION
import com.eighteen.eighteenandroid.data.HEADER_REFRESH
import com.eighteen.eighteenandroid.data.REFRESH_TOKEN_PREFERENCE_KEY
import com.eighteen.eighteenandroid.data.datasource.remote.request.SchoolRequest
import com.eighteen.eighteenandroid.data.datasource.remote.request.SignUpRequest
import com.eighteen.eighteenandroid.data.datasource.remote.service.UserService
import com.eighteen.eighteenandroid.data.mapper.ApiException
import com.eighteen.eighteenandroid.data.mapper.mapper
import com.eighteen.eighteenandroid.domain.model.AuthToken
import com.eighteen.eighteenandroid.domain.model.Mbti
import com.eighteen.eighteenandroid.domain.model.Media
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.model.Qna
import com.eighteen.eighteenandroid.domain.model.QnaType
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.domain.model.SignUpInfo
import com.eighteen.eighteenandroid.domain.model.SnsLink
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val preferenceDatastore: DataStore<Preferences>
) : UserRepository {
    override suspend fun fetchUserData(): Result<List<User>> =
        runCatching {
//            userService.getUserInfo().mapper {
//                it.map { userResponse ->
//                    UserMapper.asUserCaseModel(userResponse)
//                }
//            }

            listOf(
                User(
                    userImage = "https://image.blip.kr/v1/file/021ec61ff1c9936943383b84236a0e69",
                    userId = "1",
                    userName = "김 에스더",
                    userAge = "16",
                    userSchoolName = "서울 중학교",
                    tag = "운동"
                ),
                User(
                    userImage = "https://cdn.newsculture.press/news/photo/202308/529742_657577_5726.jpg",
                    userId = "2",
                    userName = "김 에스더",
                    userAge = "16",
                    userSchoolName = "부천 중학교",
                    tag = "스터디"
                ),
                User(
                    userImage = "https://mblogthumb-phinf.pstatic.net/MjAyMTEwMzFfMTY1/MDAxNjM1NjUzMTI2NjI3.xXYQteLLoWLKcR9YnXS0Hk_y-DInauMzF25g7FxlcScg.2Y-neBBMVoP2IhcwzX2Zy2HB2d8EnM_cY76FVLuk_1Yg.JPEG.ssun2415/IMG_4148.jpg?type=w800",
                    userId = "3",
                    userName = "김 에스더",
                    userAge = "16",
                    userSchoolName = "인천 중학교",
                    tag = "프로젝트"
                )
            )
        }

    override suspend fun fetchUserDetailInfo(id: String): Result<Profile> = runCatching {
//                userService.postProfileDetailInfo(id).mapper { profileDetailResponse ->
//            ProfileDetailMapper.asProfileDetailModel(profileDetailResponse = profileDetailResponse)
//        }

        // 비디오인지 여부를 결정하는 함수
        fun determineIfMedia(link: String): Boolean {
            val videoExtensions = listOf("mp4", "avi", "mkv", "mov")
            return videoExtensions.any { link.endsWith(it, ignoreCase = true) }
        }

        // 각 링크를 MediaItem으로 변환
        val mediaList = listOf(
            "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg",
            "https://contents-cdn.viewus.co.kr/image/2023/12/CP-2022-0017/image-de4d5a79-bbe3-4c2e-84a7-f36976345663.jpeg",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://cdn.newsculture.press/news/photo/202306/525899_650590_620.jpg",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        ).map { link ->
            if (determineIfMedia(link)) {
                Media.Video(link)
            } else {
                Media.Image(link)
            }
        }


        val qnaList = List(10) { index ->
            Qna(
                question = QnaType.values().getOrNull(index) ?: QnaType.QNA1,
                answer = "${index + 1}. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            )
        }

        return Result.success(
            Profile(
                nickName = "김 에스더",
                school = School("서울 중학교", "서울시"),
                age = 16,
                badgeCount = 10,
                teenDescription = "5월 2주차 우승",
                medias = mediaList,
                id = "2",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                mbti = Mbti(
                    energy = Mbti.MbtiType.Energy.Extroversion,
                    information = Mbti.MbtiType.Information.Intuition,
                    decision = Mbti.MbtiType.Decision.Feeling,
                    lifestyle = Mbti.MbtiType.Lifestyle.Judging
                ),
                qna = qnaList,
                snsLinks = listOf(SnsLink("https://www.google.co.kr", "Google")),
                birth = null,
                introduction = null
            )
        )
    }

    override suspend fun postSignUp(signUpInfo: SignUpInfo): Result<AuthToken> = runCatching {
        val signUpRequest = signUpInfo.run {
            SignUpRequest(
                phoneNumber = phoneNumber,
                uniqueId = uniqueId,
                birthDay = birthDay,
                nickName = nickName,
                schoolData = SchoolRequest(
                    schoolName = school.name,
                    schoolLocation = school.address
                ),
                category = tag.strValue
            )
        }
        userService.postSignUp(
            profileImageKeys = signUpInfo.mediaKeys,
            signUpRequest = signUpRequest
        ).headers().run {
            safeLet(get(HEADER_AUTHORIZATION), get(HEADER_REFRESH)) { accessToken, refreshToken ->
                AuthToken(accessToken = accessToken, refreshToken = refreshToken)
            } ?: throw Exception("token must not be null")
        }
    }

    override suspend fun checkIdDuplication(
        uniqueId: String
    ): Result<Boolean> = runCatching {
        userService.postDuplicationCheck(uniqueId = uniqueId).mapper {
            it.data ?: throw ApiException.Unknown
        }
    }

    override fun getTokenFlow(): Flow<AuthToken?> =
        preferenceDatastore.data.map {
            val accessToken = it[ACCESS_TOKEN_PREFERENCE_KEY]
            val refreshToken = it[REFRESH_TOKEN_PREFERENCE_KEY]
            safeLet(accessToken, refreshToken) { access, refresh ->
                AuthToken(accessToken = access, refreshToken = refresh)
            }
        }

    override suspend fun saveToken(authToken: AuthToken) {
        preferenceDatastore.edit {
            it[ACCESS_TOKEN_PREFERENCE_KEY] = authToken.accessToken
            it[REFRESH_TOKEN_PREFERENCE_KEY] = authToken.refreshToken
        }
    }

    override suspend fun login(phoneNumber: String): Result<AuthToken> = runCatching {
        val result = userService.postLogin(phoneNumber = phoneNumber)
        result.headers().run {
            safeLet(get(HEADER_AUTHORIZATION), get(HEADER_REFRESH)) { accessToken, refreshToken ->
                AuthToken(accessToken = accessToken, refreshToken = refreshToken)
            } ?: throw Exception("token must not be null")
        }
    }
}
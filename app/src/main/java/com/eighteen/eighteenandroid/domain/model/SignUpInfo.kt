package com.eighteen.eighteenandroid.domain.model

import com.eighteen.eighteenandroid.common.enums.Tag

//TODO tag 필수 필드인지 확인 필요
data class SignUpInfo(
    val phoneNumber: String,
    val uniqueId: String,
    val nickName: String,
    val birthDay: String,
    val school: School,
    val mediaKeys: List<String>,
    val tag: Tag
)
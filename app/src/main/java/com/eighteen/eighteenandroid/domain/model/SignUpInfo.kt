package com.eighteen.eighteenandroid.domain.model

import com.eighteen.eighteenandroid.common.enums.Tag

data class SignUpInfo(
    val phoneNumber: String,
    val uniqueId: String,
    val nickName: String,
    val birthDay: String,
    val school: School,
    val mediaKeys: List<String>,
    val tag: Tag
)
package com.eighteen.eighteenandroid.domain.model

class Participant(
    val id: String,
    val name: String,
    val school: String,
    val age: String,
    val imgUrl: String,
    val hasVideo: Boolean? = false,
    val winningRound: Int? = null
)
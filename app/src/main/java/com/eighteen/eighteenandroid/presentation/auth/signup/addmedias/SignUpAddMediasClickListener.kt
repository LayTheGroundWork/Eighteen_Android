package com.eighteen.eighteenandroid.presentation.auth.signup.addmedias

interface SignUpAddMediasClickListener {
    fun onClickAddMedia()
    fun onClickSetMainMedia(position: Int)
    fun onClickRemove(position: Int)
}
package com.eighteen.eighteenandroid.domain.model

sealed class MainItem{
    // 헤더
    data class HeaderView (
        val text: String
    ) : MainItem()

    // 헤더 + 더보기
    data class HeaderWithMoreView (
        val text: String
    ) : MainItem()

    // 유저 리스트
    data class UserListView (
        val userList: List<User>
    ) : MainItem()

    // 디바이더
    object DividerView : MainItem()

    // About Teen 리스트
    data class AboutTeenListView(
        val aboutTeenList: List<AboutTeen>
    ): MainItem()

    // 토너먼트 리스트
    data class TournamentListView(
        val tournamentList: List<Tournament>
    ): MainItem()

    //
    data class UserView(
        val user: User
    ): MainItem()

    // LoadingView
    object LoadingView: MainItem()
}
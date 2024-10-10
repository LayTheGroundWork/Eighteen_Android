package com.eighteen.eighteenandroid.presentation.ranking.model

sealed interface RankingItem {
    val id: String
    val category: String
    fun areItemsTheSame(other: RankingItem): Boolean
    fun areContentsTheSame(other: RankingItem): Boolean

    data class VoteCard(
        override val id: String,
        // TODO category, illustration(Drawable) Enum으로 동시에 빼기
        override val category: String,
        val illustrationUrl: String
    ) : RankingItem {
        override fun areItemsTheSame(other: RankingItem) =
            other is VoteCard && this.id == other.id

        override fun areContentsTheSame(other: RankingItem) =
            other is VoteCard && this == other
    }

    data class WinnerCard(
        override val id: String,
        override val category: String,
        val imageUrl: String,
        val title: String,
        val tournamentNumb: Int
    ) : RankingItem {
        override fun areItemsTheSame(other: RankingItem) =
            other is WinnerCard && this.id == other.id

        override fun areContentsTheSame(other: RankingItem) =
            other is WinnerCard && this == other
    }
}



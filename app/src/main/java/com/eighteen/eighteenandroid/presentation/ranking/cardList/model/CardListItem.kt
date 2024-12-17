package com.eighteen.eighteenandroid.presentation.ranking.cardList.model

sealed interface CardListItem {
    val id: String
    val category: String
    fun areItemsTheSame(other: CardListItem): Boolean
    fun areContentsTheSame(other: CardListItem): Boolean

    data class VoteCard(
        override val id: String,
        override val category: String,
        val illustrationUrl: String
    ) : CardListItem {
        override fun areItemsTheSame(other: CardListItem) =
            other is CardListItem && this.id == other.id

        override fun areContentsTheSame(other: CardListItem) =
            other is CardListItem && this == other
    }

    data class WinnerCard(
        override val id: String,
        override val category: String,
        val imageUrl: String,
        val tournamentNumb: Int
    ) : CardListItem {
        override fun areItemsTheSame(other: CardListItem) =
            other is CardListItem && this.id == other.id

        override fun areContentsTheSame(other: CardListItem) =
            other is CardListItem && this == other
    }

}

package com.eighteen.eighteenandroid.presentation.ranking.model

import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem

interface RankingCategory {
    val id: String

    fun areItemsTheSame(other: RankingCategory): Boolean
    fun areContentsTheSame(other: RankingCategory): Boolean

    data class Category(
        override val id: String,
        val categoryTitle: String,
        val cardListItems: List<CardListItem>
    ) : RankingCategory {
        override fun areItemsTheSame(other: RankingCategory): Boolean =
            other is Category && this.id == other.id

        override fun areContentsTheSame(other: RankingCategory): Boolean =
            other is Category && this == other
    }
}
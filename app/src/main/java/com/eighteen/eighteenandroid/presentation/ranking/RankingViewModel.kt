package com.eighteen.eighteenandroid.presentation.ranking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.usecase.GetTournamentCategoryInfoUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.ranking.cardList.model.CardListItem
import com.eighteen.eighteenandroid.presentation.ranking.model.RankingCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(private val getTournamentCategoryInfoUseCase: GetTournamentCategoryInfoUseCase) :
    ViewModel() {
    private val _categories =
        MutableStateFlow<ModelState<List<RankingCategory.Category>>>(ModelState.Empty())
    private var rankingJob: Job? = null
    val categories get() = _categories.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        if (rankingJob?.isCompleted == false) return

        rankingJob = viewModelScope.launch {
            _categories.value = ModelState.Loading()
            val result = getTournamentCategoryInfoUseCase()
            result.onSuccess { rankingListData ->
                val transformedCategories = rankingListData.map { rankingList ->
                    RankingCategory.Category(
                        id = rankingList.thisWeekTournamentNo,
                        categoryTitle = rankingList.category,
                        cardListItems = rankingList.winner.map { winner ->
                            CardListItem.WinnerCard(
                                id = winner.tournamentNo,
                                round = winner.round,
                                imageUrl = winner.profileImageUrl
                            )
                        }
                    )
                }
                Log.d("RankingViewModel", "transformedCategories: $transformedCategories")
                _categories.value = ModelState.Success(transformedCategories)
            }.onFailure {
                _categories.value = ModelState.Error(throwable = it)
            }
        }
    }
}

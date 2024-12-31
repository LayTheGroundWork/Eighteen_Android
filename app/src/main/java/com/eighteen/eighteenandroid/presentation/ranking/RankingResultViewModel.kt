package com.eighteen.eighteenandroid.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.model.UserRankInfo
import com.eighteen.eighteenandroid.domain.usecase.GetTournamentRankingUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RankingResultViewModel @AssistedInject constructor(
    private val getTournamentRankingUseCase: GetTournamentRankingUseCase,
    @Assisted private val tournamentNo: Int
) : ViewModel() {

    private val _rankingListStateFlow = MutableStateFlow<ModelState<List<UserRankInfo>>>(ModelState.Empty())
    val rankingListStateFlow: StateFlow<ModelState<List<UserRankInfo>>> = _rankingListStateFlow.asStateFlow()

    private var getRankingListJob: Job? = null

    init {
        getRankingResult()
    }

    private fun getRankingResult() {
        if (getRankingListJob?.isCompleted == false) return
        getRankingListJob = viewModelScope.launch {
            _rankingListStateFlow.value = ModelState.Loading()

            getTournamentRankingUseCase.invoke(tournamentNo)
                .onSuccess {
                    _rankingListStateFlow.value = ModelState.Success(it)
                }
                .onFailure {
                    _rankingListStateFlow.value = ModelState.Error(throwable = it)
                }
        }
    }


    @AssistedFactory
    interface UserRankingAssistedFactory {
        fun create(categoryId: Int): RankingResultViewModel
    }

    class Factory(
        private val assistedFactory: UserRankingAssistedFactory,
        private val tournamentNo: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(tournamentNo) as T
        }
    }

    fun clear() {
        getRankingListJob?.cancel()
        _rankingListStateFlow.value = ModelState.Empty()
    }
}
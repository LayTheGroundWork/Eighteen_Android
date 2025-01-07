package com.eighteen.eighteenandroid.presentation.ranking.votingComplete

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.usecase.SubmitTournamentResultsUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.ranking.voting.model.TournamentEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VotingCompleteViewModel @Inject constructor(
    private val submitTournamentResultsUseCase: SubmitTournamentResultsUseCase
) : ViewModel() {
    private val _topParticipants =
        MutableStateFlow<ModelState<List<TournamentEntity.Participant>>>(ModelState.Empty())
    val topParticipants = _topParticipants.asStateFlow()

    private var votingJob: Job? = null

    fun requestSubmitTopParticipantsList(
        tournamentNo: Int,
        participantIdsOrderByRank: List<String>
    ) {
        if (votingJob?.isCompleted == false) return

        votingJob = viewModelScope.launch {
            _topParticipants.value = ModelState.Loading()
            submitTournamentResultsUseCase.invoke(
                tournamentNo = tournamentNo,
                participantIdsOrderByRank = participantIdsOrderByRank
            ).onSuccess {
                _topParticipants.value = ModelState.Success()
                Log.d("VotingCompleteFragment", "onSuccess")
            }.onFailure {
                _topParticipants.value = ModelState.Error(throwable = it)
            }
        }
    }
}
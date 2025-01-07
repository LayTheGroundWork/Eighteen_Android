package com.eighteen.eighteenandroid.presentation.ranking.voting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.usecase.GetThisWeekParticipantsUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.ranking.voting.model.TournamentEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VotingViewModel @AssistedInject constructor(
    @Assisted private val thisWeekTournamentNo: Int,
    @Assisted private val category: String,
    private val getThisWeekParticipantsUseCase: GetThisWeekParticipantsUseCase
) : ViewModel() {
    private val _currentMatch = MutableStateFlow<TournamentEntity.Match?>(null)
    val currentMatch = _currentMatch.asStateFlow()

    private val _isRoundComplete =
        MutableStateFlow<Pair<Boolean, TournamentEntity.Participant?>>(false to null)
    val isRoundComplete = _isRoundComplete.asStateFlow()

    private val _thisWeekParticipants =
        MutableStateFlow<ModelState<List<TournamentEntity.Participant>>>(ModelState.Empty())
    val thisWeekParticipants = _thisWeekParticipants.asStateFlow()

    private val _progress = MutableStateFlow<ModelState<Int>>(ModelState.Empty())
    val progress = _progress.asStateFlow()


    private var votingJob: Job? = null

    private var participants: List<TournamentEntity.Participant> = listOf()
    private var matches: List<TournamentEntity.Match> = listOf()
    var currentRound = 16
    private var currentMatchIndex = 0


    init {
        setupParticipants()
        setupMatches()
        showCurrentMatch()


//        fetchThisWeekParticipants(category = category)
    }

    private fun fetchThisWeekParticipants(category: String) {
        if (votingJob?.isCompleted == false) return

        votingJob = viewModelScope.launch {
            _thisWeekParticipants.value = ModelState.Loading()
            getThisWeekParticipantsUseCase.invoke(category = category)
                .onSuccess { thisWeekParticipantsData ->
                    val thisWeekParticipantsModel = thisWeekParticipantsData.map {
                        TournamentEntity.Participant(
                            id = it.id,
                            nickName = it.name,
                            school = it.school,
                            age = it.age,
                            imageUrl = it.imgUrl
                        )
                    }
                    Log.d(
                        "VotingViewModel",
                        "thisWeekParticipantsModel: $thisWeekParticipantsModel"
                    )
                    _thisWeekParticipants.value = ModelState.Success(thisWeekParticipantsModel)
                }.onFailure {
                    _thisWeekParticipants.value = ModelState.Error(throwable = it)
                }
        }
    }

    fun setupParticipants() {
        participants = listOf(
            TournamentEntity.Participant(
                "1",
                "참가자1",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "2",
                "참가자2",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "3",
                "참가자3",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "4",
                "참가자4",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "5",
                "참가자5",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "6",
                "참가자6",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "7",
                "참가자7",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "8",
                "참가자8",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "9",
                "참가자9",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "10",
                "참가자10",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "11",
                "참가자11",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "12",
                "참가자12",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "13",
                "참가자13",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "14",
                "참가자14",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "15",
                "참가자15",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            ),
            TournamentEntity.Participant(
                "16",
                "참가자16",
                "서울 중학교",
                "16세",
                "https://cdn.seoulwire.com/news/photo/202109/450631_649892_1740.jpg"
            )
        )
    }

    private fun setProgress() {
        val totalMatches = 15 // 16강 기준 전체 경기 수
        val completedMatches = (16 - currentRound) + currentMatchIndex // 진행된 경기 수
        val progressValue = ((completedMatches.toDouble() / totalMatches) * 100).toInt()

        _progress.value = ModelState.Success(progressValue)
    }

    private fun setupMatches() {
        matches = participants.chunked(2).map { pair ->
            TournamentEntity.Match(pair[0], pair[1])
        }
    }

    private fun showCurrentMatch() {
        if (currentMatchIndex < matches.size) {
            _currentMatch.value = matches[currentMatchIndex]
        }
    }

    fun selectWinner(winner: TournamentEntity.Participant) {
        matches[currentMatchIndex].winner = winner

        if (currentMatchIndex < matches.size - 1) {
            currentMatchIndex++
            showCurrentMatch()
        } else {
            currentRound /= 2
            currentMatchIndex = 0
            setupNextRound()
        }
        setProgress()
        Log.d("VotingViewModel", currentRound.toString())
    }

    private fun setupNextRound() {
        currentMatchIndex = 0

        val winners = matches.mapNotNull { it.winner }

        Log.d("VotingViewModel", "Current round updated to $currentRound")

        if (winners.size == 1) {
            _isRoundComplete.value = true to winners.first()
        } else {
            // 다음 라운드의 매치를 구성
            matches = winners.chunked(2).map { pair -> TournamentEntity.Match(pair[0], pair[1]) }
            showCurrentMatch()
        }
    }

    @AssistedFactory
    interface VotingAssistedFactory {
        fun create(thisWeekTournamentNo: Int, category: String): VotingViewModel
    }

    class Factory(
        private val assistedFactory: VotingAssistedFactory,
        private val thisWeekTournamentNo: Int,
        private val category: String
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(
                thisWeekTournamentNo = thisWeekTournamentNo,
                category = category
            ) as T
        }
    }
}
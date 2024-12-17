package com.eighteen.eighteenandroid.presentation.ranking.voting

import androidx.lifecycle.ViewModel
import com.eighteen.eighteenandroid.presentation.ranking.voting.model.TournamentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VotingViewModel : ViewModel() {
    private val _currentMatch = MutableStateFlow<TournamentEntity.Match?>(null)
    val currentMatch = _currentMatch.asStateFlow()

    private val _isRoundComplete =
        MutableStateFlow<Pair<Boolean, TournamentEntity.Participant?>>(false to null)

    val isRoundComplete = _isRoundComplete.asStateFlow()

    private var participants: List<TournamentEntity.Participant> = listOf()
    private var matches: List<TournamentEntity.Match> = listOf()
    private var currentRound = 16
    private var currentMatchIndex = 0

    init {
        setupParticipants()
        setupMatches()
        showCurrentMatch()
    }

    fun setupParticipants() {
        participants = listOf(
            TournamentEntity.Participant("1", "참가자1"),
            TournamentEntity.Participant("2", "참가자2"),
            TournamentEntity.Participant("3", "참가자3"),
            TournamentEntity.Participant("4", "참가자4"),
            TournamentEntity.Participant("5", "참가자5"),
            TournamentEntity.Participant("6", "참가자6"),
            TournamentEntity.Participant("7", "참가자7"),
            TournamentEntity.Participant("8", "참가자8"),
            TournamentEntity.Participant("9", "참가자9"),
            TournamentEntity.Participant("10", "참가자10"),
            TournamentEntity.Participant("11", "참가자11"),
            TournamentEntity.Participant("12", "참가자12"),
            TournamentEntity.Participant("13", "참가자13"),
            TournamentEntity.Participant("14", "참가자14"),
            TournamentEntity.Participant("15", "참가자15"),
            TournamentEntity.Participant("16", "참가자16")
        )
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
            setupNextRound()
        }
    }

    private fun setupNextRound() {
        currentRound /= 2
        currentMatchIndex = 0
        val winners = matches.mapNotNull { it.winner }

        if (winners.size == 1) {
            _isRoundComplete.value = true to winners.first()
        } else {
            // 다음 라운드의 매치를 구성
            matches = winners.chunked(2).map { pair -> TournamentEntity.Match(pair[0], pair[1]) }
            showCurrentMatch()
        }
    }
}
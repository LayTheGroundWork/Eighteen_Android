package com.eighteen.eighteenandroid.presentation.myprofile.setting.managinginfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.usecase.DeleteUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.SignOutUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagingInfoViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {
    private val _signOutEventChannel = Channel<ModelState<String>>(Channel.BUFFERED)
    val signOutEventChannel = _signOutEventChannel.receiveAsFlow()

    private val _deleteUserEventChannel = Channel<ModelState<String>>(Channel.BUFFERED)
    val deleteUserEventChannel = _deleteUserEventChannel.receiveAsFlow()

    private var managingInfoJob: Job? = null
    fun signOut() {
        if (managingInfoJob?.isCompleted == false) return
        managingInfoJob = viewModelScope.launch {
            _signOutEventChannel.send(ModelState.Loading())
            signOutUseCase.invoke().onSuccess {
                _signOutEventChannel.send(ModelState.Success(it))
            }.onFailure {
                _signOutEventChannel.send(ModelState.Error(throwable = it))
            }
        }
    }

    fun deleteUser() {
        if (managingInfoJob?.isCompleted == false) return
        managingInfoJob = viewModelScope.launch {
            _deleteUserEventChannel.send(ModelState.Loading())
            deleteUserUseCase.invoke().onSuccess {
                _deleteUserEventChannel.send(ModelState.Success(it))
            }.onFailure {
                _deleteUserEventChannel.send(ModelState.Error(throwable = it))
            }
        }
    }
}
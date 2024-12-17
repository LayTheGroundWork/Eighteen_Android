package com.eighteen.eighteenandroid.presentation.auth.signup.enterauthcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.usecase.ConfirmMessageUseCase
import com.eighteen.eighteenandroid.domain.usecase.LoginUseCase
import com.eighteen.eighteenandroid.domain.usecase.SendMessageUseCase
import com.eighteen.eighteenandroid.presentation.auth.signup.enterauthcode.model.ConfirmResultModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SignUpEnterAuthCodeViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val confirmMessageUseCase: ConfirmMessageUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _sendMessageResultStateFlow =
        MutableStateFlow<ModelState<Long>>(ModelState.Success(Calendar.getInstance().timeInMillis))
    val sendMessageResultStateFlow: StateFlow<ModelState<Long>> =
        _sendMessageResultStateFlow.asStateFlow()

    private var requestSendMessageJob: Job? = null

    private val _confirmMessageResultStateFlow =
        MutableStateFlow<ModelState<ConfirmResultModel>>(ModelState.Empty())
    val confirmMessageResultStateFlow: StateFlow<ModelState<ConfirmResultModel>> =
        _confirmMessageResultStateFlow.asStateFlow()

    private var requestConfirmMessageJob: Job? = null

    val remainTimeFlow = flow {
        while (true) {
            sendMessageResultStateFlow.value.data?.let {
                val diff = Calendar.getInstance().timeInMillis - it
                val remainTimeSec = (MAXIMUM_REMAIN_TIME_MIL_SEC - diff) / 1000
                if (remainTimeSec < 0) return@let
                val min = remainTimeSec / 60
                val sec = remainTimeSec % 60
                val resultString = String.format("%02d:%02d", min, sec)
                emit(resultString)
            } ?: run {
                emit("00:00")
            }
            delay(1000)
        }
    }

    fun requestSendMessage(phoneNumber: String) {
        if (requestSendMessageJob?.isCompleted == false) return
        requestSendMessageJob = viewModelScope.launch {
            _sendMessageResultStateFlow.value = ModelState.Loading()
            sendMessageUseCase.invoke(phoneNumber = phoneNumber).onSuccess {
                _sendMessageResultStateFlow.value =
                    ModelState.Success(Calendar.getInstance().timeInMillis)
            }.onFailure {
                _sendMessageResultStateFlow.value = ModelState.Error(throwable = it)
            }
        }
    }

    fun requestConfirmMessage(phoneNumber: String, verificationCode: String) {
        if (requestConfirmMessageJob?.isCompleted == false) return
        requestConfirmMessageJob = viewModelScope.launch {
            _confirmMessageResultStateFlow.value = ModelState.Loading()
            confirmMessageUseCase.invoke(
                phoneNumber = phoneNumber,
                verificationCode = verificationCode
            ).onSuccess {
                loginUseCase.invoke(phoneNumber = phoneNumber).onSuccess {
                    _confirmMessageResultStateFlow.value =
                        ModelState.Success(ConfirmResultModel.LoginSuccess(it))
                }.onFailure {
                    _confirmMessageResultStateFlow.value =
                        ModelState.Success(ConfirmResultModel.LoginFailed)
                }
            }.onFailure {
                _confirmMessageResultStateFlow.value = ModelState.Error(throwable = it)
            }
        }
    }

    fun clear() {
        requestSendMessageJob?.cancel()
        requestConfirmMessageJob?.cancel()
        _confirmMessageResultStateFlow.value = ModelState.Empty()
    }

    companion object {
        private const val MAXIMUM_REMAIN_TIME_MIL_SEC = 180000
    }
}
package com.eighteen.eighteenandroid.presentation.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpEditMediaAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpMedia
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.common.livedata.Event
import com.eighteen.eighteenandroid.presentation.editmedia.model.EditMediaResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

//TODO 로그인, 회원가입 관련 api 연동, 입력받은 정보 저장 뒤로가기 시 데이터 지워진 상태로 나옴
@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel(), SignUpViewModelContentInterface {
    private val _actionEventLiveData = MutableLiveData<Event<SignUpAction>>()
    val actionEventLiveData: LiveData<Event<SignUpAction>> = _actionEventLiveData

    private val _nextButtonLiveData = MutableLiveData<SignUpNextButtonModel>()
    val nextButtonLiveData: LiveData<SignUpNextButtonModel> = _nextButtonLiveData

    //진행도 표시 0일경우 비어있음, 100일경우 최대, null일 경우 gone
    private val _progressLiveData = MutableLiveData<Int?>(null)
    val progressLiveData: LiveData<Int?> = _progressLiveData

    private val _editMediaActionEventLiveData = MutableLiveData<Event<SignUpEditMediaAction>>()
    val editMediaActionEventLiveData: LiveData<Event<SignUpEditMediaAction>> =
        _editMediaActionEventLiveData

    override var id: String = ""
    override var nickName: String = ""
    override var birth: Date = Date()
    override var school: String = ""

    private val _mediasStateFlow = MutableStateFlow<List<SignUpMedia>>(emptyList())
    override val mediasStateFlow: StateFlow<List<SignUpMedia>> = _mediasStateFlow

    fun actionToPrevPage() {
        _actionEventLiveData.value = Event(SignUpAction.PREV)
    }

    fun actionToNextPage() {
        _actionEventLiveData.value = Event(SignUpAction.NEXT)
    }

    override fun setNextButtonModel(signUpNextButtonModel: SignUpNextButtonModel) {
        _nextButtonLiveData.value = signUpNextButtonModel
    }

    override fun setEditMediaAction(editMediaAction: SignUpEditMediaAction) {
        _editMediaActionEventLiveData.value = Event(editMediaAction)
    }

    /**
     * @param progress : 진행도 표시 값 (0~100) null일 경우 gone
     */
    fun setProgress(progress: Int?) {
        _progressLiveData.value = progress
    }

    fun addMediaResult(mediaResult: EditMediaResult) {
        val signUpMedia = when (mediaResult) {
            is EditMediaResult.Image -> SignUpMedia.Image(
                imageBitmap = mediaResult.imageBitmap,
                tags = mediaResult.tags
            )
            is EditMediaResult.Video -> SignUpMedia.Video(
                thumbnailBitmap = mediaResult.thumbnailBitmap,
                tags = mediaResult.tags
            )
        }
        _mediasStateFlow.value = _mediasStateFlow.value.toMutableList().apply {
            add(signUpMedia)
        }
    }

    override fun clearMediaResultStateFlow() {
        _mediasStateFlow.value = emptyList()
    }

    override fun onCleared() {
        _mediasStateFlow.value.forEach {
            val targetBitmap = when (it) {
                is SignUpMedia.Image -> it.imageBitmap
                is SignUpMedia.Video -> it.thumbnailBitmap
            }
            targetBitmap.recycle()
        }
        super.onCleared()
    }

}
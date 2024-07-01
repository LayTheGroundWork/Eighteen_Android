package com.eighteen.eighteenandroid.presentation.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.common.livedata.EventObserver

/**
 * 회원가입 내용 fragment, 네비게이션 호출을 위해 반드시 해당클래스 구현해서 써야 함
 * @property onMoveNextPageAction : 다음 페이지로 이동 버튼 누를 때 실행하는 action
 * @property onMovePrevPageAction : 이전 페이지 혹은 백버튼 누를 때 실행하는 action
 * @property progress : progress bar 진행도 (0~100), null일 경우 gone
 * @property signUpNextButtonModel : 하단 다음버튼 디자인 모델
 */
//TODO back pressed handle 구현
abstract class BaseSignUpContentFragment<VB : ViewBinding>(bindingFactory: (LayoutInflater) -> VB) :
    BaseFragment<VB>(bindingFactory) {

    private val signUpViewModel by viewModels<SignUpViewModel>(ownerProducer = {
        getContainerFragment() ?: this
    })

    protected val signUpViewModelContentInterface: SignUpViewModelContentInterface
        get() = signUpViewModel

    protected open val onMovePrevPageAction: () -> Unit = {
        findNavController().popBackStack()
    }
    protected abstract val onMoveNextPageAction: () -> Unit
    protected abstract val progress: Int?
    protected abstract val signUpNextButtonModel: SignUpNextButtonModel

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initActionObserver()
        initButtonValue()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initButtonValue() {
        signUpViewModel.run {
            setProgress(progress = progress)
            setNextButtonModel(signUpNextButtonModel = signUpNextButtonModel)
        }
    }

    private fun initActionObserver() {
        signUpViewModel.actionEventLiveData.observe(viewLifecycleOwner, EventObserver { action ->
            when (action) {
                SignUpAction.NEXT -> onMoveNextPageAction.invoke()
                SignUpAction.PREV -> onMovePrevPageAction.invoke()
            }
        })
    }

    private fun getContainerFragment(): Fragment? {
        var parent = parentFragment
        while (parent != null) {
            if (parent is SignUpContentContainer) break
            parent = parent.parentFragment
        }
        return parent
    }
}
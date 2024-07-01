package com.eighteen.eighteenandroid.presentation.auth.signup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpEditMediaAction
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.common.livedata.EventObserver
import com.eighteen.eighteenandroid.presentation.common.viewModelsByBackStackEntry
import com.eighteen.eighteenandroid.presentation.editmedia.BaseEditMediaFragment.Companion.EDIT_MEDIA_POP_DESTINATION_ID_KEY
import com.eighteen.eighteenandroid.presentation.editmedia.EditMediaViewModel

/**
 * 회원가입 기능의 진입점
 * 내부의 ChildFragment에 대해 navigation graph 소유
 */
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate),
    SignUpContentContainer {
    private val signUpViewModel by viewModels<SignUpViewModel>()

    private val editMediaViewModel by viewModelsByBackStackEntry<EditMediaViewModel>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.fcvSignUpContentContainer) as? NavHostFragment
            val backStackEntryCount =
                navHostFragment?.childFragmentManager?.backStackEntryCount ?: 0
            if (backStackEntryCount <= 0) findNavController().popBackStack()
            else signUpViewModel.actionToPrevPage()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    override fun initView() {
        bind {
            tvBtnNext.setOnClickListener {
                signUpViewModel.actionToNextPage()
            }
            ivBtnBack.setOnClickListener {
                signUpViewModel.actionToPrevPage()
            }
        }
        initSignUpObserver()
        initEditMediaObserver()
    }

    private fun initSignUpObserver() = with(signUpViewModel) {
        bind {
            progressLiveData.observe(viewLifecycleOwner) {
                with(lpbProgress) {
                    isVisible = (it != null)
                    if (it != null) {
                        progress = it
                    }
                }

            }

            nextButtonLiveData.observe(viewLifecycleOwner) {
                with(tvBtnNext) {
                    isVisible = it.isVisible
                    isEnabled = it.isEnabled
                    text = context?.getText(it.buttonText.strRes)
                    val textColor = ContextCompat.getColor(
                        context,
                        if (it.isEnabled) R.color.white else R.color.grey_02
                    )
                    setTextColor(textColor)
                    layoutParams = layoutParams.apply {
                        width =
                            if (it.size == SignUpNextButtonModel.Size.FULL) LayoutParams.MATCH_PARENT else LayoutParams.WRAP_CONTENT
                    }
                }
            }

            editMediaActionEventLiveData.observe(
                viewLifecycleOwner,
                EventObserver { action ->
                    val navActionId = when (action) {
                        is SignUpEditMediaAction.EditImage -> R.id.action_fragmentSignUp_to_fragmentEditImage
                        is SignUpEditMediaAction.EditVideo -> R.id.action_fragmentSignUp_to_fragmentEditVideo
                    }
                    val bundle = Bundle().apply {
                        putInt(EDIT_MEDIA_POP_DESTINATION_ID_KEY, R.id.fragmentSignUp)
                    }
                    editMediaViewModel.setMediaUriString(uriString = action.uriString)
                    findNavController().navigate(navActionId, bundle)
                })
        }
    }

    private fun initEditMediaObserver() = with(editMediaViewModel) {
        editResultEventLiveData.observe(viewLifecycleOwner, EventObserver {
            signUpViewModel.addMediaResult(it)
        })
    }
}
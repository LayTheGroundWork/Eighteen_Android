package com.eighteen.eighteenandroid.presentation.auth.signup.enterschool

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentSignUpEnterSchoolBinding
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.presentation.auth.signup.BaseSignUpContentFragment
import com.eighteen.eighteenandroid.presentation.auth.signup.model.SignUpNextButtonModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.dp2Px
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpEnterSchoolFragment :
    BaseSignUpContentFragment<FragmentSignUpEnterSchoolBinding>(FragmentSignUpEnterSchoolBinding::inflate) {
    override val onMovePrevPageAction: () -> Unit = {
        signUpViewModelContentInterface.birth = null
        super.onMovePrevPageAction.invoke()
    }
    override val onMoveNextPageAction: () -> Unit = {
        findNavController().navigate(R.id.action_fragmentSignUpEnterSchool_to_fragmentSignUpSelectTag)
    }
    override val progress: Int = 80
    override val signUpNextButtonModel = SignUpNextButtonModel(
        isVisible = true,
        isEnabled = true,
        size = SignUpNextButtonModel.Size.NORMAL,
        buttonText = SignUpNextButtonModel.ButtonText.NEXT
    )

    private val signUpEnterSchoolViewModel by viewModels<SignUpEnterSchoolViewModel>()

    private var textChangeJob: Job? = null

    override fun initView() {
        initEditText()
        initRecyclerView()
        initStateFlow()
    }

    private fun initEditText() = with(binding.etInput) {
        setOnFocusChangeListener { _, _ ->
            updateSearchResultVisibility()
        }
        addTextChangedListener {
            val keyword = it?.toString() ?: ""
            if (keyword != signUpViewModelContentInterface.school?.name) clearSelectedSchool()
            if (keyword.isBlank()) return@addTextChangedListener
            textChangeJob?.cancel()
            textChangeJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(SEARCH_DEBOUNCE_MIL_SEC)
                signUpEnterSchoolViewModel.requestGetSchools(schoolName = keyword)
            }
            updateSearchResultVisibility()
        }
        signUpViewModelContentInterface.school?.let {
            setText(it.name)
        }
        updateButtonModel()
        updateSearchResultVisibility()
    }

    private fun updateSearchResultVisibility() = bind {
        cvSearchResultContainer.isVisible =
            etInput.isFocused && etInput.text?.toString().isNullOrBlank().not()
    }

    private fun initRecyclerView() = with(binding.rvSearchResult) {
        adapter = SignUpEnterSchoolSearchResultAdapter(onClickSchool = ::onClickSchool)
        val dividerColorInt = ContextCompat.getColor(context, R.color.divider)
        val dividerHeightPx = context.dp2Px(DIVIDER_HEIGHT_DP)
        addItemDecoration(
            SignUpEnterSchoolSearchResultItemDecoration(
                dividerColor = dividerColorInt,
                dividerHeightPx = dividerHeightPx
            )
        )
    }

    private fun onClickSchool(school: School) {
        signUpViewModelContentInterface.school = school
        updateButtonModel()
        binding.etInput.setText(school.name)
        binding.etInput.clearFocus()
    }

    private fun clearSelectedSchool() {
        signUpViewModelContentInterface.school = null
        updateButtonModel()
    }

    private fun updateButtonModel() {
        signUpViewModelContentInterface.setNextButtonModel(
            signUpNextButtonModel = signUpNextButtonModel.copy(
                isEnabled = signUpViewModelContentInterface.school != null
            )
        )
    }

    private fun initStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpEnterSchoolViewModel.schoolsStateFlow.collect {
                    when (it) {
                        is ModelState.Loading -> {
                            //TODO Loading 처리
                        }
                        is ModelState.Success -> {
                            //TODO empty case 처리
                            bind {
                                (rvSearchResult.adapter as? SignUpEnterSchoolSearchResultAdapter)?.submitList(
                                    it.data?.schools
                                )
                            }

                        }
                        is ModelState.Error -> {
                            //TODO Error 처리
                        }
                        is ModelState.Empty -> {
                            //do nothing
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MIL_SEC = 500L
        private const val DIVIDER_HEIGHT_DP = 1
    }
}
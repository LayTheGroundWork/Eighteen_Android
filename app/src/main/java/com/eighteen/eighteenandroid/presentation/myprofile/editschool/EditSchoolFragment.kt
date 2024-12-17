package com.eighteen.eighteenandroid.presentation.myprofile.editschool

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentEditSchoolBinding
import com.eighteen.eighteenandroid.domain.model.School
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.dp2Px
import com.eighteen.eighteenandroid.presentation.common.searchschool.SchoolSearchResultAdapter
import com.eighteen.eighteenandroid.presentation.common.searchschool.SchoolSearchResultItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditSchoolFragment :
    BaseFragment<FragmentEditSchoolBinding>(FragmentEditSchoolBinding::inflate) {
    private val myViewModel by activityViewModels<MyViewModel>()

    private val editSchoolViewModel by viewModels<EditSchoolViewModel>()

    private var textChangeJob: Job? = null
    override fun initView() {
        initEditText()
        initRecyclerView()
        bind {
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvBtnComplete.setOnClickListener {
                myViewModel.requestEditSchool(school = editSchoolViewModel.selectedSchool)
            }
        }
        initStateFlow()
    }

    private fun initEditText() = with(binding.inEditSchool.etInput) {
        setOnFocusChangeListener { _, _ ->
            updateSearchResultVisibility()
        }
        addTextChangedListener {
            val keyword = it?.toString()
            if (keyword.isNullOrBlank()) return@addTextChangedListener
            textChangeJob?.cancel()
            textChangeJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(SEARCH_DEBOUNCE_MIL_SEC)
                editSchoolViewModel.requestGetSchools(schoolName = keyword)
            }
            updateSearchResultVisibility()

        }
        editSchoolViewModel.selectedSchool?.let { school ->
            setText(school.name)
        }
        updateSearchResultVisibility()
    }

    private fun updateSearchResultVisibility() = bind {
        with(inEditSchool) {
            cvSearchResultContainer.isVisible =
                etInput.isFocused && etInput.text?.toString().isNullOrBlank().not()
        }
    }

    private fun initRecyclerView() = with(binding.inEditSchool.rvSearchResult) {
        adapter = SchoolSearchResultAdapter(::onClickSchool)
        addItemDecoration(
            SchoolSearchResultItemDecoration(
                dividerColor = ContextCompat.getColor(
                    context,
                    R.color.divider
                ), dividerHeightPx = context.dp2Px(DIVIDER_HEIGHT_DP)
            )
        )
    }

    private fun initStateFlow() {
        collectInLifecycle(editSchoolViewModel.schoolsStateFlow, viewLifecycleOwner) {
            when (it) {
                is ModelState.Loading -> {
                    //TODO Loading 처리
                }
                is ModelState.Success -> {
                    //TODO empty case 처리
                    (binding.inEditSchool.rvSearchResult.adapter as? SchoolSearchResultAdapter)?.submitList(
                        it.data?.schools
                    )
                }
                is ModelState.Error -> {
                    //TODO Error 처리
                }
                is ModelState.Empty -> {
                    //do nothing
                }
            }
        }
        collectInLifecycle(myViewModel.editProfileEventStateFlow) {
            when (it) {
                is ModelState.Loading -> {
                    //TODO 로딩
                }
                is ModelState.Success -> {
                    it.data?.getContentIfNotHandled()?.let {
                        findNavController().popBackStack()
                    }
                }
                is ModelState.Error -> {
                    //TODO 에러
                }
                is ModelState.Empty -> {
                    //do nothing
                }
            }
        }
    }

    private fun onClickSchool(school: School) {
        editSchoolViewModel.selectedSchool = school
        binding.inEditSchool.etInput.setText(school.name)
        binding.inEditSchool.etInput.clearFocus()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MIL_SEC = 500L
        private const val DIVIDER_HEIGHT_DP = 1
    }
}
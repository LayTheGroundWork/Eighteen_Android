package com.eighteen.eighteenandroid.presentation.myprofile.edittenofqna

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentEditTenOfQnaBinding
import com.eighteen.eighteenandroid.databinding.ItemEditTenOfQnaInputBinding
import com.eighteen.eighteenandroid.domain.model.Qna
import com.eighteen.eighteenandroid.domain.model.QnaType
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.common.viewModelsByBackStackEntry
import com.eighteen.eighteenandroid.presentation.dialog.selectqna.SelectQnaDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.selectqna.model.SelectQnaDialogModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTenOfQnaFragment :
    BaseFragment<FragmentEditTenOfQnaBinding>(FragmentEditTenOfQnaBinding::inflate) {

    private val myViewModel by activityViewModels<MyViewModel>()

    private val editTenOfQnaViewModel by viewModelsByBackStackEntry<EditTenOfQnaViewModel>(
        factoryProducer = {
            EditTenOfQnaViewModel.Factory(
                initQnas = myViewModel.myProfileStateFlow.value.data?.qna ?: emptyList()
            )
        })

    override fun initView() {
        bind {
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvBtnAddQna.setOnClickListener {
                openSelectQnaDialog()
            }
            tvBtnSave.setOnClickListener {
                myViewModel.requestEditQuestions(questions = editTenOfQnaViewModel.editTenOfQnaModelsStateFlow.value)
            }
        }
        initStateFlow()
        initFragmentResult()
    }

    private fun openSelectQnaDialog() {
        val disableQnaSet =
            editTenOfQnaViewModel.editTenOfQnaModelsStateFlow.value.map { it.question }.toSet()
        val dialogModels = QnaType.values().map { qnaType ->
            SelectQnaDialogModel(
                qnaType = qnaType,
                isEnabled = disableQnaSet.contains(qnaType).not()
            )
        }
        val selectQnaDialog = SelectQnaDialogFragment.newInstance(
            requestKey = REQUEST_KEY_SELECT_DIALOG,
            title = getString(R.string.my_profile_edit_ten_of_qna_select_title),
            models = dialogModels
        )
        showDialogFragment(dialogFragment = selectQnaDialog)
    }

    private fun initStateFlow() {
        collectInLifecycle(editTenOfQnaViewModel.editTenOfQnaModelsStateFlow) {
            bind {
                llQnas.run {
                    removeAllViews()
                    isVisible = it.isNotEmpty()
                    it.forEachIndexed { index, qna ->
                        val qnaView =
                            createQnaView(model = qna, isDividerVisible = index < it.lastIndex)
                        addView(qnaView)
                    }
                }
                tvBtnAddQna.isVisible = it.size < MAXIMUM_QNA_SIZE
                tvBtnSave.isEnabled = it.size >= MINIMUM_QNA_SIZE
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

    private fun createQnaView(model: Qna, isDividerVisible: Boolean) =
        ItemEditTenOfQnaInputBinding.inflate(layoutInflater).apply {
            //TODO qna 질문 적용
            tvQuestion.text = model.question.name
            tvAnswer.text = model.answer
            vDivider.visibility = if (isDividerVisible) View.VISIBLE else View.INVISIBLE
            ivBtnRemove.setOnClickListener {
                editTenOfQnaViewModel.removeQna(qnaType = model.question)
            }
        }.root

    private fun initFragmentResult() {
        childFragmentManager.setFragmentResultListener(REQUEST_KEY_SELECT_DIALOG,
            viewLifecycleOwner,
            object : SelectQnaDialogFragment.SelectQnaResultListener() {
                override fun onSelectResult(qnaType: QnaType) {
                    val bundle =
                        bundleOf(
                            EditTenOfQnaAnswerFragment.ARGUMENT_QNA_TYPE_KEY to qnaType,
                            EditTenOfQnaAnswerFragment.ARGUMENT_DESTINATION_ID_KEY to R.id.fragmentEditTenOfQna
                        )
                    findNavController().navigate(R.id.fragmentEditTenOfQnaAnswer, bundle)
                }
            })
    }

    companion object {
        private const val REQUEST_KEY_SELECT_DIALOG = "request_key_select_dialog"
        private const val MINIMUM_QNA_SIZE = 3
        private const val MAXIMUM_QNA_SIZE = 10
    }
}
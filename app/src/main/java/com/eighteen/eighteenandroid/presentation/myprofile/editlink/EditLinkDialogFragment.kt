package com.eighteen.eighteenandroid.presentation.myprofile.editlink

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentEditLinkDialogBinding
import com.eighteen.eighteenandroid.domain.model.SnsInfo
import com.eighteen.eighteenandroid.presentation.BaseDialogFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import com.eighteen.eighteenandroid.presentation.dialog.ErrorDialogFragment
import com.eighteen.eighteenandroid.presentation.myprofile.editlink.model.EditLinkModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLinkDialogFragment :
    BaseDialogFragment<FragmentEditLinkDialogBinding>(FragmentEditLinkDialogBinding::inflate) {

    private val myViewModel by activityViewModels<MyViewModel>()

    private val editLinkViewModel by viewModels<EditLinkViewModel>(factoryProducer = {
        val snsLinksList = myViewModel.myProfileStateFlow.value.data?.snsInfoList ?: emptyList()
        EditLinkViewModel.Factory(
            instagram = snsLinksList.find { it.type == SnsInfo.SnsType.INSTAGRAM }?.id.orEmpty(),
            x = snsLinksList.find { it.type == SnsInfo.SnsType.X }?.id.orEmpty(),
            tiktok = snsLinksList.find { it.type == SnsInfo.SnsType.TIKTOK }?.id.orEmpty(),
            youtube = snsLinksList.find { it.type == SnsInfo.SnsType.YOUTUBE }?.id.orEmpty(),
        )
    })

    override fun initView() {
        bind {
            with(editLinkViewModel.editLinkModelStateFlow.value) {
                etInstagram.run {
                    setText(instagram)
                    addTextChangedListener {
                        editLinkViewModel.setInstagram(it?.toString().orEmpty())
                    }
                }
                etTiktok.run {
                    setText(tiktok)
                    addTextChangedListener {
                        editLinkViewModel.setTiktok(it?.toString().orEmpty())
                    }
                }
                etX.run {
                    setText(this@with.x)
                    addTextChangedListener {
                        editLinkViewModel.setX(it?.toString().orEmpty())
                    }
                }
                etYoutube.run {
                    setText(youtube)
                    addTextChangedListener {
                        editLinkViewModel.setYoutube(it?.toString().orEmpty())
                    }
                }
            }
            ivBtnCheck.setOnClickListener {
                myViewModel.requestEditSnsInfo(snsInfo = editLinkViewModel.editLinkModelStateFlow.value.toSnsInfoList())
            }
            ivBtnBack.setOnClickListener {
                dismissNow()
            }
        }
        initStateFlow()
    }


    private fun EditLinkModel.toSnsInfoList(): List<SnsInfo> {
        val instagramSnsInfo = SnsInfo(type = SnsInfo.SnsType.INSTAGRAM, id = this.instagram)
        val xSnsInfo = SnsInfo(type = SnsInfo.SnsType.X, id = this.x)
        val tiktokSnsInfo = SnsInfo(type = SnsInfo.SnsType.TIKTOK, id = this.tiktok)
        val youtubeSnsInfo = SnsInfo(type = SnsInfo.SnsType.YOUTUBE, id = this.youtube)
        return listOf(
            instagramSnsInfo,
            xSnsInfo,
            tiktokSnsInfo,
            youtubeSnsInfo
        ).filter { it.id.isNotEmpty() }
    }

    private fun initStateFlow() {
        collectInLifecycle(editLinkViewModel.editLinkModelStateFlow) { editLinkModel ->
            val context = context ?: return@collectInLifecycle
            val isEnabled = editLinkModel.run { listOf(instagram, x, tiktok, youtube) }
                .any { text -> text.isNotEmpty() }
            binding.ivBtnCheck.isEnabled = isEnabled
            val targetDrawable =
                ContextCompat.getDrawable(context, R.drawable.ic_check) ?: return@collectInLifecycle
            val colorTint =
                ContextCompat.getColor(context, if (isEnabled) R.color.black else R.color.grey_03)
            DrawableCompat.setTint(targetDrawable, colorTint)
            binding.ivBtnCheck.setImageDrawable(targetDrawable)
        }
        collectInLifecycle(myViewModel.editProfileEventStateFlow) {
            binding.inLoading.root.visibility =
                if (it is ModelState.Loading) View.VISIBLE else View.INVISIBLE
            when (it) {
                is ModelState.Success -> {
                    it.data?.getContentIfNotHandled()?.let {
                        dismissNow()
                    }
                }
                is ModelState.Error -> {
                    it.data?.getContentIfNotHandled()?.let {
                        showDialogFragment(ErrorDialogFragment())
                    }
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initDialogWindow()
    }

    private fun initDialogWindow() {
        dialog?.run {
            window?.run {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }
}
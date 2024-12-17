package com.eighteen.eighteenandroid.presentation.myprofile.editmedia

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.GridLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.MEDIA_COUNT
import com.eighteen.eighteenandroid.databinding.FragmentMyEditMediaBinding
import com.eighteen.eighteenandroid.databinding.ItemEditMediaBinding
import com.eighteen.eighteenandroid.domain.model.Media
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.common.collectInLifecycle
import com.eighteen.eighteenandroid.presentation.common.dp2Px
import com.eighteen.eighteenandroid.presentation.common.getMimeTypeFromUri
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader
import com.eighteen.eighteenandroid.presentation.common.livedata.EventObserver
import com.eighteen.eighteenandroid.presentation.common.mediapicker.MediaPickerWrapper
import com.eighteen.eighteenandroid.presentation.common.requestPermissions
import com.eighteen.eighteenandroid.presentation.common.viewModelsByBackStackEntry
import com.eighteen.eighteenandroid.presentation.editmedia.BaseEditMediaFragment
import com.eighteen.eighteenandroid.presentation.editmedia.EditMediaViewModel
import com.eighteen.eighteenandroid.presentation.editmedia.model.EditMediaResult
import com.eighteen.eighteenandroid.presentation.myprofile.editmedia.model.MyEditMediaEvent
import com.eighteen.eighteenandroid.presentation.myprofile.editmedia.model.MyEditMediaModel

class MyEditMediaFragment :
    BaseFragment<FragmentMyEditMediaBinding>(FragmentMyEditMediaBinding::inflate) {
    private val myViewModel by activityViewModels<MyViewModel>()
    private val myEditMediaViewModel by viewModels<MyEditMediaViewModel>(factoryProducer = {
        //TODO profile 모델 대표이미지 구분 문의 후 적용(현재 임시로 변환)
        val medias = myViewModel.myProfileStateFlow.value.data?.medias ?: emptyList()
        val mediaModels = medias.map {
            when (it) {
                is Media.Image -> MyEditMediaModel.Media.Image(imageUrl = it.url)
                is Media.Video -> MyEditMediaModel.Media.Video(url = it.url)
            }
        }
        val myEditMediaModel = MyEditMediaModel(mainMedia = null, medias = mediaModels)
        MyEditMediaViewModel.Factory(myEditMediaModel)
    })

    private val editMediaViewModel by viewModelsByBackStackEntry<EditMediaViewModel>()

    private val mediaPickerWrapper = MediaPickerWrapper(this)

    override fun initView() {
        bind {
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvBtnComplete.setOnClickListener {
                //TODO 수정 요청 api
            }
        }
        initStateFlow()
        initObserver()
    }

    private fun initStateFlow() {
        collectInLifecycle(myEditMediaViewModel.mediasStateFlow) {
            binding.glContainer.run {
                removeAllViews()
                it.medias.forEachIndexed { idx, media ->
                    val mediaView =
                        createMediaView(
                            model = media,
                            isMain = it.mainMediaOrFirst == media,
                            position = idx
                        )
                    addView(mediaView)
                }
                repeat(MEDIA_COUNT - it.medias.size) {
                    addView(createEmptyView())
                }
                val betweenMargin = context.dp2Px(16)
                children.forEachIndexed { index, view ->
                    view.updateLayoutParams<MarginLayoutParams> {
                        val left = if (index % 2 == 1) betweenMargin / 2 else 0
                        val right = if (index % 2 == 0) betweenMargin / 2 else 0
                        setMargins(left, 0, right, betweenMargin)
                    }
                }
            }
        }
    }

    private fun createMediaView(model: MyEditMediaModel.Media, isMain: Boolean, position: Int) =
        ItemEditMediaBinding.inflate(layoutInflater).apply {
            ivBtnDelete.isVisible = true
            ivVideoIcon.isVisible = model is MyEditMediaModel.Media.Video
            ivBtnSetMainMedia.isVisible = true
            ivBtnSetMainMedia.alpha = if (isMain) 1f else 0.2f
            when (model) {
                is MyEditMediaModel.Media.Image -> run {
                    if (model.imageUrl != null) {
                        ImageLoader.get().loadUrlCenterCrop(ivMedia, url = model.imageUrl)
                    } else if (model.bitmap != null) {
                        ivMedia.setImageBitmap(model.bitmap)
                    } else {
                        //TODO placeholder
                    }
                }
                is MyEditMediaModel.Media.Video -> ImageLoader.get()
                    .loadUrlCenterCrop(ivMedia, url = model.url)
            }
            ivVideoIcon.isVisible = model is MyEditMediaModel.Media.Video
            ivBtnDelete.setOnClickListener {
                myEditMediaViewModel.removeMedia(model)
            }
            ivBtnSetMainMedia.setOnClickListener {
                myEditMediaViewModel.setMainMedia(position = position)
            }
        }.root.apply {
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }

    private fun createEmptyView() =
        ItemEditMediaBinding.inflate(layoutInflater).apply {
            ivBtnDelete.isVisible = false
            ivVideoIcon.isVisible = false
            ivBtnSetMainMedia.isVisible = false
        }.root.apply {
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            setOnClickListener {
                mediaPickerWrapper.openMediaPicker {
                    val uri = it ?: return@openMediaPicker
                    getMimeTypeFromUri(uri, context)?.let { typeString ->
                        val uriString = uri.toString()
                        editMediaViewModel.setMediaUriString(uriString = uriString)
                        val event =
                            if (typeString.startsWith("image")) MyEditMediaEvent.Image(uri = uriString)
                            else if (typeString.startsWith("video")) MyEditMediaEvent.Video(uri = uriString)
                            else return@let
                        myEditMediaViewModel.setOpenEditMediaEvent(event = event)
                    }
                }
            }
        }

    private fun initObserver() {
        myEditMediaViewModel.openEditMediaEventLiveData.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle().apply {
                putInt(
                    BaseEditMediaFragment.EDIT_MEDIA_POP_DESTINATION_ID_KEY,
                    R.id.fragmentMyEditMedia
                )
            }
            when (it) {
                is MyEditMediaEvent.Image -> {
                    findNavController().navigate(
                        R.id.fragmentEditImage,
                        bundle
                    )
                }
                is MyEditMediaEvent.Video -> {
                    val onGranted = {
                        findNavController().navigate(
                            R.id.fragmentEditVideo,
                            bundle
                        )
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        onGranted.invoke()
                    } else {
                        //TODO 권한요청 거부 시 작업(팝업 or 세팅 이동 등) 논의
                        requestPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            onGranted = onGranted
                        )
                    }
                }
            }
        })
        editMediaViewModel.editResultEventLiveData.observe(viewLifecycleOwner, EventObserver {
            val model = when (it) {
                is EditMediaResult.Image -> MyEditMediaModel.Media.Image(bitmap = it.imageBitmap)
                is EditMediaResult.Video -> MyEditMediaModel.Media.Video(url = it.uriString)
            }
            myEditMediaViewModel.addMedia(model)
        })
    }
}
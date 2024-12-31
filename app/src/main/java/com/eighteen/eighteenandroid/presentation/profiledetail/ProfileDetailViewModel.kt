package com.eighteen.eighteenandroid.presentation.profiledetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.model.Profile
import com.eighteen.eighteenandroid.domain.usecase.GetUserDetailInfoUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.profiledetail.model.ProfileDetailModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileDetailViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val getUserDetailInfoUseCase: GetUserDetailInfoUseCase
) : ViewModel() {
    private val _items = MutableStateFlow<ModelState<List<ProfileDetailModel>>>(ModelState.Empty())
    val items get() = _items.asStateFlow()

    val profileInfo
        get() = items.value.data?.filterIsInstance<ProfileDetailModel.ProfileInfo>()?.firstOrNull()

    private var showAllItems = false

    private var qnaItems = emptyList<ProfileDetailModel.Qna>()

    private var _currentPosition = 0
    val currentPosition: Int get() = _currentPosition

    private val _isLike = MutableStateFlow(false)
    val isLike: StateFlow<Boolean> get() = _isLike
    private var profileDetailJob: Job? = null

    init {
        fetchProfileDetail()
    }

    fun fetchProfileDetail() {
        if (profileDetailJob?.isCompleted == false) return

        profileDetailJob = viewModelScope.launch {
            _items.value = ModelState.Loading()
            val result = getUserDetailInfoUseCase(userId)
            result.onSuccess { profile ->
                _items.value = ModelState.Success(mapProfileToItems(profile))
            }.onFailure {
                _items.value = ModelState.Error(throwable = it)
            }
            Log.d("getUserDetailInfoUseCase", "@@@@@ : ${getUserDetailInfoUseCase(userId)}")
        }
    }

    private fun mapProfileToItems(profile: Profile): List<ProfileDetailModel> {
        val qnaList = if (profile.qna.isEmpty()) {
            listOf(
                ProfileDetailModel.Qna(
                    id = "empty_qna",
                    question = "",
                    answer = ""
                )
            )
        } else {
            profile.qna.map { qnaItem ->
                ProfileDetailModel.Qna(
                    id = qnaItem.question.name,
                    question = qnaItem.question.name,
                    answer = qnaItem.answer
                )
            }
        }
        return listOf(
            ProfileDetailModel.ProfileImages(
                id = profile.id,
                mediaItems = profile.medias.map {
                    ProfileDetailModel.MediaItem(
                        url = it.url,
                        isVideo = true
                    )
                },
                likeCount = 100
            ),
            ProfileDetailModel.ProfileInfo(
                id = profile.id,
                name = profile.nickName,
                age = profile.age,
                school = profile.school?.name ?: ""
            ),
            ProfileDetailModel.BadgeAndTeen(
                id = profile.id,
                badgeCount = profile.badgeCount,
                teenAward = profile.teenDescription ?: ""
            ),
            ProfileDetailModel.Introduction(
                id = profile.id,
                personalityType = "Unknown",
                introductionText = profile.description ?: "등록된 소개글이 없습니다."
            ),
            ProfileDetailModel.QnaListTitle(id = "qna_list_title"),
        ) + qnaList
    }

    fun setCurrentPosition(position: Int) {
        _currentPosition = position
    }

    fun toggleItems() {
        showAllItems = !showAllItems
        updateItems()
    }


    private fun updateItems() {
        if (_items.value.isSuccess().not()) return

        val currentItems = (_items.value.data ?: emptyList()).toMutableList()
        val qnaIndex = currentItems.indexOfFirst { it is ProfileDetailModel.QnaToggle }
        val qnaTitleIndex = currentItems.indexOfFirst { it is ProfileDetailModel.QnaListTitle }

        if (qnaIndex != -1) {
            val qnaList = qnaItems
            val showAll = showAllItems
            val qnaItemsToShow = if (showAll) qnaList else qnaList.take(ITEM_COUNT_THRESHOLD)

            currentItems.removeAll { it is ProfileDetailModel.Qna }

            // QnaListTitle 뒤에 Qna 항목들을 추가합니다.
            val insertIndex = qnaTitleIndex + 1
            if (insertIndex <= currentItems.size) {
                currentItems.addAll(insertIndex, qnaItemsToShow)
            } else {
                currentItems.addAll(qnaItemsToShow)
            }

            // 기존의 QnaToggle 항목이 있으면 제거합니다.
            val existingToggleIndex =
                currentItems.indexOfFirst { it is ProfileDetailModel.QnaToggle }
            if (existingToggleIndex != -1) {
                currentItems.removeAt(existingToggleIndex)
            }

            // Qna 항목들 뒤에 QnaToggle 항목을 추가합니다.
            if (qnaList.size > ITEM_COUNT_THRESHOLD) {
                val insertIndex = qnaIndex + 1 + qnaItemsToShow.size
                if (insertIndex <= currentItems.size) {
                    currentItems.add(
                        insertIndex,
                        ProfileDetailModel.QnaToggle(
                            id = "toggle",
                            isExpanded = showAllItems
                        )
                    )
                } else {
                    currentItems.add(
                        ProfileDetailModel.QnaToggle(
                            id = "toggle",
                            isExpanded = showAllItems
                        )
                    )
                }
            }
            _items.value = ModelState.Success(currentItems)
        }
    }

    fun updateQnaItems(qnaItems: List<ProfileDetailModel.Qna>) {
        this.qnaItems = qnaItems
        updateItems()
    }

    fun toggleLike() {
        if (_items.value.isSuccess().not()) return

        val currentItems = (_items.value.data ?: emptyList()).toMutableList()
        val profileImages =
            currentItems.filterIsInstance<ProfileDetailModel.ProfileImages>().firstOrNull()

        profileImages?.let { item ->
            val isCurrentLiked = item.isLiked
            val updateProfileImages = item.copy(
                likeCount = if (isCurrentLiked) profileImages.likeCount - 1 else profileImages.likeCount + 1,
                isLiked = !isCurrentLiked
            )
            val index = currentItems.indexOf(item)
            if (index != -1) {
                currentItems[index] = updateProfileImages
                _items.value = ModelState.Success(currentItems)

                _isLike.value = updateProfileImages.isLiked
            }
        }
    }

    @AssistedFactory
    interface ProfileDetailAssistedFactory {
        fun create(userId: String): ProfileDetailViewModel
    }

    class Factory(
        private val assistedFactory: ProfileDetailAssistedFactory,
        private val getUserDetailInfoUseCase: GetUserDetailInfoUseCase,
        private val userId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(userId = userId) as T
        }
    }

    companion object {
        const val ITEM_COUNT_THRESHOLD = 2
    }
}
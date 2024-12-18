package com.eighteen.eighteenandroid.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.common.ResourceProvider
import com.eighteen.eighteenandroid.common.USER_TYPE_SIGNIN
import com.eighteen.eighteenandroid.common.USER_TYPE_GUEST
import com.eighteen.eighteenandroid.common.enums.Tag
import com.eighteen.eighteenandroid.domain.model.AboutTeen
import com.eighteen.eighteenandroid.domain.model.MainItem
import com.eighteen.eighteenandroid.domain.model.Tournament
import com.eighteen.eighteenandroid.domain.usecase.GetUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetAuthTokenFlowUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val getUserUseCase: GetUserUseCase,
    private val getAuthTokenFlowUseCase: GetAuthTokenFlowUseCase
) : ViewModel() {

    var popularUserPosition = 0
    var pageScrollPosition = 0

    private val _mainItemStateFlow = MutableStateFlow<ModelState<List<MainItem>>>(ModelState.Empty())
    val mainItemStateFlow: StateFlow<ModelState<List<MainItem>>>
        get() = _mainItemStateFlow.asStateFlow()

    /** 현재 페이지 정보 */
    private var currentPage = 0

    /** 전체 페이지 수 */
    private val _totalPage = MutableLiveData<Int>()
    val totalPage: LiveData<Int>
        get() = _totalPage

    /** Category tag */
    private val _category = MutableLiveData<Tag>()
    val category: LiveData<Tag>
        get() = _category

    init {
        initMain(Tag.ALL)
    }

    /** 화면 초기화 */
    private fun resetPage() {
        currentPage = 0
    }

    fun setPage(page: Int) {
        currentPage = page
    }

    fun initMain(category: Tag) {
        val items = mutableListOf<MainItem>()

        viewModelScope.launch {
            _mainItemStateFlow.value = ModelState.Loading()

            resetPage()

            // 로그인 상태 확인
            val authTokenStateFlow = getAuthTokenFlowUseCase.invoke().stateIn(
                viewModelScope,
                SharingStarted.Eagerly, null
            )

            // TODO. val popularTeenInitialState (인기 Teen)

            items.addAll(
                listOf(
                    MainItem.HeaderView(resourceProvider.getString(R.string.main_today_teen)),
                    MainItem.UserListView(
                        emptyList()
                    ), // TODO. 인기 Teen
                    MainItem.DividerView,
                    MainItem.HeaderView(resourceProvider.getString(R.string.main_about_teen)),
                    MainItem.AboutTeenListView(
                        listOf(
                            AboutTeen("Teen", "친구들의 프로필을 투표해보세요!"),
                            AboutTeen("토너먼트", "투표 결과를 한 눈에 볼 수 있어요!"),
                            AboutTeen("채팅", "채팅을 통해 친구들과 소통해보세요!"),
                            AboutTeen("나만의 Teen", "나만의 프로필을 등록해보세요!")
                        )
                    ), // About Teen List
                    MainItem.DividerView,
                    MainItem.HeaderWithMoreView(resourceProvider.getString(R.string.main_tournament_in_progress)),
                    MainItem.TournamentListView(
                        listOf(
                            Tournament.Exercise,
                            Tournament.Study
                        )
                    ), // TODO. Tournament List
                    MainItem.DividerView,
                    MainItem.HeaderView(resourceProvider.getString(R.string.main_another_teen))
                )
            )

            val anotherTeenInitialState = if(authTokenStateFlow.value == null) {
                // 게스트
                fetchAnotherUser(category, USER_TYPE_GUEST, 0).await()
            } else {
                // 유저
                fetchAnotherUser(category, USER_TYPE_SIGNIN, 0).await()
            }

            anotherTeenInitialState.onSuccess {
                _totalPage.postValue(it.totalPageCount) // 총 페이지

                it.users.forEach { user ->
                    items.add(
                        MainItem.UserView(user)
                    )
                }

                _mainItemStateFlow.value = ModelState.Success(items)
            }.onFailure { e ->
                _mainItemStateFlow.value = ModelState.Error(throwable = e)
            }


//            if( userDataState is ModelState.Success && aboutTeenDataState is ModelState.Success ) {
//                // updateMain
//            } else {
//                // 에러화면
//            }
        }
    }

    private fun fetchAnotherUser(category: Tag, userType: String, page: Int) = viewModelScope.async {
        getUserUseCase.invoke(category.name, userType, page)
    }

//    private suspend fun fetchUserData() = viewModelScope.async {
//        userUseCase.invoke().onSuccess {
////            ModelState.Success(it)
//            _userData.value = it
//        }.onFailure { e ->
//            Log.e(TAG, e.toString())
////            ModelState.Error(e)
//        }
//    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
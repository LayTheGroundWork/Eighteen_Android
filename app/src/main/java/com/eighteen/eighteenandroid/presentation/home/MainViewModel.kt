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

    /** 메인화면 전체 데이터 */
    private val _mainItemStateFlow = MutableStateFlow<ModelState<List<MainItem>>>(ModelState.Empty())
    val mainItemStateFlow: StateFlow<ModelState<List<MainItem>>>
        get() = _mainItemStateFlow.asStateFlow()

    /** 무한 스크롤을 위한 새로 불러온 데이터 */
    private val _appendStateFlow = MutableStateFlow<ModelState<List<MainItem>>>(ModelState.Empty())
    val appendStateFlow: StateFlow<ModelState<List<MainItem>>>
        get() = _appendStateFlow.asStateFlow()

    /** 남은 페이지 목록 */
    private val _pageNumList = MutableLiveData<MutableList<Int>>()
    val pageNumList: LiveData<MutableList<Int>>
        get() = _pageNumList

    init {
        initMain(Tag.ALL)
    }

    fun removePage(page: Int) {
        val pages = _pageNumList.value
        pages?.let {
            it.remove(page)
            _pageNumList.postValue(it)
        }
    }

    fun initMain(category: Tag) {
        val items = mutableListOf<MainItem>()

        viewModelScope.launch {
            _mainItemStateFlow.value = ModelState.Loading()

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

            // 로그인 상태 확인
            val authTokenStateFlow = getAuthTokenFlowUseCase.invoke().stateIn(
                viewModelScope,
                SharingStarted.Eagerly, null
            )

            val anotherTeenInitialState = if(authTokenStateFlow.value == null) {
                // 게스트
                fetchAnotherUser(category, USER_TYPE_GUEST, 0).await()
            } else {
                // 유저
                fetchAnotherUser(category, USER_TYPE_SIGNIN, 0).await()
            }

            anotherTeenInitialState.onSuccess {
                if(it.totalPageCount > 1) {
                    val pages = mutableListOf<Int>()

                    // 첫 번째 페이지(0)를 제외한 페이지 목록
                    for (page in 1 until it.totalPageCount) {
                        pages.add(page)
                    }

                    _pageNumList.postValue(pages)
                }

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

    /** 또 다른 Teen 무한 스크롤, 다음 페이지 유저 정보 가져오기 */
    fun requestNextPage(category: Tag, page: Int) {
        viewModelScope.launch {
            val items = mutableListOf<MainItem>()

            // 로딩 시작
            _appendStateFlow.value = ModelState.Loading()

            // 로그인 상태 확인
            val authTokenStateFlow = getAuthTokenFlowUseCase.invoke().stateIn(
                viewModelScope,
                SharingStarted.Eagerly, null
            )

            val anotherTeenInitialState = if(authTokenStateFlow.value == null) {
                // 게스트
                fetchAnotherUser(category, USER_TYPE_GUEST, page).await()
            } else {
                // 유저
                fetchAnotherUser(category, USER_TYPE_SIGNIN, page).await()
            }

            anotherTeenInitialState.onSuccess {
                it.users.forEach { user ->
                    items.add(
                        MainItem.UserView(user)
                    )
                }

                _appendStateFlow.value = ModelState.Success(items)
            }.onFailure { e ->
                _appendStateFlow.value = ModelState.Error(throwable = e)
            }
        }
    }

    private fun fetchAnotherUser(category: Tag, userType: String, page: Int) = viewModelScope.async {
        getUserUseCase.invoke(category.name, userType, page)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
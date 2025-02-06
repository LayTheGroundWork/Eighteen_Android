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
import com.eighteen.eighteenandroid.data.mapper.ApiException
import com.eighteen.eighteenandroid.domain.model.AboutTeen
import com.eighteen.eighteenandroid.domain.model.MainItem
import com.eighteen.eighteenandroid.domain.model.Tournament
import com.eighteen.eighteenandroid.domain.usecase.GetAnotherUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetAuthTokenFlowUseCase
import com.eighteen.eighteenandroid.domain.usecase.GetPopularUserUseCase
import com.eighteen.eighteenandroid.domain.usecase.UserLikeUseCase
import com.eighteen.eighteenandroid.presentation.common.ModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val getAnotherUserUseCase: GetAnotherUserUseCase,
    private val getPopularUserUseCase: GetPopularUserUseCase,
    private val getAuthTokenFlowUseCase: GetAuthTokenFlowUseCase,
    private val userLikeUseCase: UserLikeUseCase
) : ViewModel() {

    var popularUserPosition = 0

    /** 메인화면 전체 데이터 */
    private val _mainItemStateFlow =
        MutableStateFlow<ModelState<List<MainItem>>>(ModelState.Empty())
    val mainItemStateFlow: StateFlow<ModelState<List<MainItem>>>
        get() = _mainItemStateFlow.asStateFlow()

    /** 남은 페이지 목록 */
    private val _pageNumList = MutableLiveData<MutableList<Int>>()
    val pageNumList: LiveData<MutableList<Int>>
        get() = _pageNumList

    /** 좋아요 */
    private val _userLikeStateFlow = MutableStateFlow<ModelState<Int>>(ModelState.Empty())
    val userLikeStateFlow: StateFlow<ModelState<Int>>
        get() = _userLikeStateFlow.asStateFlow()

    /** 좋아요 취소 */
    private val _userLikeCancelStateFlow = MutableStateFlow<ModelState<Int>>(ModelState.Empty())
    val userLikeCancelStateFlow: StateFlow<ModelState<Int>>
        get() = _userLikeCancelStateFlow.asStateFlow()

    init {
        initMain(Tag.ALL)
    }

    fun initMain(category: Tag) = viewModelScope.launch {
        val items = mutableListOf<MainItem>()
        _mainItemStateFlow.value = ModelState.Loading()

        delay(1500)
        // AuthToken 여부 확인 후 Fetch
        val authToken = getAuthTokenFlowUseCase.invoke().firstOrNull()

        // 인기 Teen
        val popularTeenInitialState = if(authToken != null) {
            getPopularUser(category, USER_TYPE_SIGNIN)
        } else {
            getPopularUser(category, USER_TYPE_GUEST)
        }

        popularTeenInitialState.onSuccess {
            with(items) {
                add(MainItem.HeaderView(resourceProvider.getString(R.string.main_today_teen)))
                add(MainItem.UserListView(it))
                add(MainItem.DividerView)
            }
        }.onFailure {
            _mainItemStateFlow.value = ModelState.Error(throwable = it)
        }

        with(items) {
            add(MainItem.HeaderView(resourceProvider.getString(R.string.main_about_teen)))
            add(MainItem.AboutTeenListView(
                listOf(
                    AboutTeen("Teen", "친구들의 프로필을 투표해보세요!"),
                    AboutTeen("토너먼트", "투표 결과를 한 눈에 볼 수 있어요!"),
                    AboutTeen("채팅", "채팅을 통해 친구들과 소통해보세요!"),
                    AboutTeen("나만의 Teen", "나만의 프로필을 등록해보세요!")
                )
            ))
            add(MainItem.DividerView)
        }

        // TODO. 토너먼트 목록 API 연결
        // val tournamentInitialState

        with(items) {
            add(MainItem.HeaderWithMoreView(resourceProvider.getString(R.string.main_tournament_in_progress)))
            add(MainItem.TournamentListView(
                listOf(
                    Tournament.Exercise,
                    Tournament.Study
                )
            ))
            add(MainItem.DividerView)
        }

        // 또 다른 Teen
        val anotherTeenInitialState = if (authToken != null) {
            getAnotherUser(category, USER_TYPE_SIGNIN, 0)
        } else {
            getAnotherUser(category, USER_TYPE_GUEST, 0)
        }

        anotherTeenInitialState.onSuccess {
            if (it.totalPageCount > 1) {
                val pages = mutableListOf<Int>()

                // 첫 번째 페이지(0)를 제외한 페이지 목록
                for (page in 1 until it.totalPageCount) {
                    pages.add(page)
                }

                _pageNumList.postValue(pages)
            }

            with(items) {
                add(MainItem.HeaderView(resourceProvider.getString(R.string.main_another_teen)))

                it.users.forEach { user ->
                    add(MainItem.UserView(user))
                }
            }
        }.onFailure { e ->
            _mainItemStateFlow.value = ModelState.Error(throwable = e)
        }

        // 모두 가져오는 데에 성공하면
        if(anotherTeenInitialState.isSuccess && popularTeenInitialState.isSuccess) {
            _mainItemStateFlow.value = ModelState.Success(items)
        } else {
            _mainItemStateFlow.value = ModelState.Error(throwable = ApiException.Unknown)
        }
    }

    /** 또 다른 Teen 무한 스크롤, 다음 페이지 유저 정보 가져오기 */
    fun requestNextPage(category: Tag, page: Int) {
        viewModelScope.launch {
            val items = _mainItemStateFlow.value.data?.toMutableList()  // 기존 값

            // 로딩 시작
            _mainItemStateFlow.value = ModelState.Loading()
            delay(1000)

            // AuthToken 여부 확인 후 Fetch
            val authToken = getAuthTokenFlowUseCase.invoke().firstOrNull()
            val anotherTeenInitialState = if (authToken != null) {
                getAnotherUser(category, USER_TYPE_SIGNIN, page)
            } else {
                getAnotherUser(category, USER_TYPE_GUEST, page)
            }

            anotherTeenInitialState
                .onSuccess {
                    it.users.forEach { user ->
                        items?.add(
                            MainItem.UserView(user)
                        )
                    }

                    _mainItemStateFlow.value = ModelState.Success(items)
                }
                .onFailure { e ->
                    _mainItemStateFlow.value = ModelState.Error(throwable = e)
                }
        }
    }

    fun removePage(page: Int) {
        val pages = _pageNumList.value
        pages?.let {
            it.remove(page)
            _pageNumList.postValue(it)
        }
    }

    private suspend fun getAnotherUser(category: Tag, userType: String, page: Int) =
        viewModelScope.async {
            getAnotherUserUseCase.invoke(category.name, userType, page)
        }.await()

    private suspend fun getPopularUser(category: Tag, userType: String) =
        viewModelScope.async {
            getPopularUserUseCase.invoke(category.name, userType)
        }.await()

    fun requestUserLike(isLike: Boolean, likedId: Int) {
        viewModelScope.launch {
            if (isLike) { // 좋아요
                _userLikeStateFlow.value = ModelState.Loading()

                postLikeUser(likedId).await()
                    .onSuccess {
                        _userLikeStateFlow.value = ModelState.Success(likedId)
                    }
                    .onFailure {
                        _userLikeStateFlow.value = ModelState.Error(throwable = it)
                    }

            } else { // 좋아요 취소
                _userLikeCancelStateFlow.value = ModelState.Loading()

                postLikeCancelUser(likedId).await()
                    .onSuccess {
                        _userLikeCancelStateFlow.value = ModelState.Success(likedId)
                    }
                    .onFailure {
                        _userLikeCancelStateFlow.value = ModelState.Error(throwable = it)
                    }
            }
        }
    }

    private fun postLikeUser(likedId: Int) = viewModelScope.async {
        userLikeUseCase.invoke(true, likedId)
    }

    private fun postLikeCancelUser(likedId: Int) = viewModelScope.async {
        userLikeUseCase.invoke(false, likedId)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
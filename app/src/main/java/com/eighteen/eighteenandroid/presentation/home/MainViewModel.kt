package com.eighteen.eighteenandroid.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eighteen.eighteenandroid.domain.model.MainItem
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.domain.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    var popularUserPosition = 0
    var pageScrollPosition = 0

    private val _userData = MutableLiveData<List<User>>()
    val userData: LiveData<List<User>> = _userData

    private val _mainItems = MutableLiveData<List<MainItem>>()
    val mainItems: LiveData<List<MainItem>>
        get() = _mainItems

    init {
        updateMain()
    }

    private fun updateMain() {
        val items = mutableListOf<MainItem>()

        // 둘 중 하나 에러나면 다시 전체 가져오기

        viewModelScope.launch {
            fetchUserData()
//            val userDataState = fetchUserData().await()
//            val aboutTeenDataState = fetchUserData().await()
            // 토너먼트
            // 1페이지

//            if( userDataState is ModelState.Success && aboutTeenDataState is ModelState.Success ) {
//
//                // updateMain
//            } else {
//                // 에러화면
//            }

            _mainItems.value = items
        }
    }

    /*fun 무한_스크롤() {
        val items = mainItems.value // 기존 데이터

        if(로딩 중) {
            item + 로딩 뷰
        } else {
            items.add(새로 받아온 놈들)
        }

        _mainItems.value = items
    }*/

    private suspend fun fetchUserData() = viewModelScope.async {
        userUseCase.invoke().onSuccess {
//            ModelState.Success(it)
            _userData.value = it
        }.onFailure { e ->
            Log.e(TAG, e.toString())
//            ModelState.Error(e)
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
package com.eighteen.eighteenandroid.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.eighteen.eighteenandroid.databinding.FragmentSearchBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private lateinit var searchUserAdapter: SearchUserAdapter
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {
        searchUserAdapter = SearchUserAdapter(
            onHeartClicked = { user ->
                // 좋아요 버튼 클릭 시
            }
        )

        searchHistoryAdapter = SearchHistoryAdapter()

        bind {
            rvSearchResult.adapter = searchUserAdapter
            rvHistory.adapter = searchHistoryAdapter
        }
    }

    override fun initView() {
        bind {
            etInput.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, after: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, after: Int) {
                    s?.let { str ->
                        // 입력 값이 있으면 -> 유저 검색 결과 가져오기
                        if (str.isNotEmpty()) {
                            rvHistory.visibility = View.GONE
                            rvSearchResult.visibility = View.VISIBLE

                            searchUserAdapter.updateList(listOf())  // TODO. 연관 검색 결과 가져오기

                            // 검색 기록 숨기기
                            tvSearchHistoryHeader.visibility = View.GONE
                            btnDelete.visibility = View.GONE
                        } else { // 입력 값 없으면 -> 검색 기록 보여주기
                            rvHistory.visibility = View.VISIBLE
                            rvSearchResult.visibility = View.GONE

                            searchHistoryAdapter.updateList(listOf())   // TODO. 검색 기록 가져오기
                            // 검색 기록
                            tvSearchHistoryHeader.visibility = View.VISIBLE
                            // 검색 기록이 있으면
                            btnDelete.visibility = View.VISIBLE
                        }
                    }
                }
            })
        }
    }
}
package com.eighteen.eighteenandroid.presentation.chat

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.FragmentChatBinding
import com.eighteen.eighteenandroid.presentation.BaseFragment
import com.eighteen.eighteenandroid.presentation.MyViewModel
import com.eighteen.eighteenandroid.presentation.chat.chatroom.ChatRoomFragment.Companion.ARGUMENT_CHAT_ROOM_ID_KEY
import com.eighteen.eighteenandroid.presentation.chat.chatroom.ChatRoomFragment.Companion.ARGUMENT_SENDER_ID_KEY
import com.eighteen.eighteenandroid.presentation.common.ModelState
import com.eighteen.eighteenandroid.presentation.common.hideKeyboardAndRemoveCurrentFocus
import com.eighteen.eighteenandroid.presentation.common.showDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {
    private val myViewModel by activityViewModels<MyViewModel>()

    @Inject
    lateinit var chatAssistedFactory: ChatViewModel.ChatAssistedFactory

    //TODO senderNo 대응하는 필드 확인(현재 임시로 적용)
    private val senderNo = 1

    private val chatViewModel by viewModels<ChatViewModel>(factoryProducer = {
        ChatViewModel.Factory(
            assistedFactory = chatAssistedFactory,
            senderNo = senderNo
        )
    })

    override fun initView() {
        bind {
            rvChatRooms.adapter = ChatRoomsAdapter(
                setSwipeState = chatViewModel::setSwipeState,
                getSwipeState = chatViewModel::getSwipeState,
                onClickExit = ::onClickExitBtn,
                onClickChatRoom = ::onClickChatRoom
            )
            rvChatRooms.itemAnimator = null
            root.setOnClickListener {
                hideKeyboardAndRemoveCurrentFocus()
            }
            etSearch.setText(
                chatViewModel.chatRoomWithKeywordStateFlow.value.data?.keyword ?: ""
            )
            etSearch.addTextChangedListener {
                chatViewModel.setKeyword(it?.run { toString() } ?: "")
            }
        }
        initStateFlow()
        initFragmentResultListener()
    }

    private fun initStateFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.chatRoomWithKeywordStateFlow.collect {
                    when (it) {
                        is ModelState.Loading -> {
                            //TODO 로딩
                        }
                        is ModelState.Success -> {
                            //TODO 비어있을 경우 처리
                            (binding.rvChatRooms.adapter as? ChatRoomsAdapter)?.submitList(
                                it.data?.chatRooms ?: emptyList()
                            )
                        }
                        is ModelState.Error -> {
                            //TODO 에러처리
                        }
                        else -> {
                            //do nothing
                        }
                    }
                }
            }
        }
    }

    private fun onClickExitBtn(chatRoomId: String) {
        val exitDialog = ChatRoomExitDialogFragment.newInstance(
            requestKey = REQUEST_KEY_CHAT_ROOM_EXIT,
            id = chatRoomId
        )
        showDialogFragment(exitDialog)
    }

    private fun initFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY_CHAT_ROOM_EXIT,
            viewLifecycleOwner,
            object : ChatRoomExitDialogFragment.ResultListener() {
                override fun onConfirm(id: String) {
                    chatViewModel.exitChatRoom(chatRoomId = id)
                }
            }
        )
    }

    private fun onClickChatRoom(chatRoomId: String) {
        val bundle = Bundle().apply {
            putString(ARGUMENT_CHAT_ROOM_ID_KEY, chatRoomId)
            putInt(ARGUMENT_SENDER_ID_KEY, senderNo)
        }
        findNavController().navigate(R.id.action_fragmentChat_to_fragmentChatRoom, bundle)
    }

    companion object {
        private const val REQUEST_KEY_CHAT_ROOM_EXIT = "REQUEST_KEY_CHAT_ROOM_EXIT"
    }
}
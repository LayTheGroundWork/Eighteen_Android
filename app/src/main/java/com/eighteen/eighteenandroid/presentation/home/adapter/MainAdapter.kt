package com.eighteen.eighteenandroid.presentation.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ItemDividerBinding
import com.eighteen.eighteenandroid.databinding.ItemMainAboutTeenListviewBinding
import com.eighteen.eighteenandroid.databinding.ItemMainHeaderBinding
import com.eighteen.eighteenandroid.databinding.ItemMainHeaderMoreBinding
import com.eighteen.eighteenandroid.databinding.ItemMainLoadingBinding
import com.eighteen.eighteenandroid.databinding.ItemMainPopularTeenListviewBinding
import com.eighteen.eighteenandroid.databinding.ItemMainTournamentListviewBinding
import com.eighteen.eighteenandroid.databinding.ItemTeenBinding
import com.eighteen.eighteenandroid.domain.model.MainItem
import com.eighteen.eighteenandroid.domain.model.Tournament
import com.eighteen.eighteenandroid.domain.model.User
import com.eighteen.eighteenandroid.presentation.home.adapter.diffcallback.MainItemDiffCallBack

interface MainAdapterListener {
    /** 유저 클릭 -> 유저 상세로 이동 */
    fun onUserClicks(user: User)

    /** 유저 좋아요 클릭 */
    fun onUserLikeClicks(likeBtn: ImageButton, user: User)

    /** 유저 채팅 클릭 */
    fun onUserChatClicks(user: User)

    /** 유저 더보기 클릭 */
    fun onUserMoreClicks(itemView: View, user: User)

    /** About Teen 클릭 */
    fun onAboutTeenClicks(title: String)

    /** 토너먼트 클릭 */
    fun onTournamentClicks(tournament: Tournament)

    /** 토너먼트 더보기 클릭 */
    fun onTournamentMoreClicks()

    /** 이전에 보여진 유저 스크롤 */
    fun scrollToPreviousUser()

    /** 이전에 보여진 유저 포지션 저장*/
    fun saveUserPosition(position: Int)

    fun startAutoScroll()

    fun stopAutoScroll()
}
class MainAdapter(
    private val context: Context,
    private val listener: MainAdapterListener
) : ListAdapter<MainItem, MainAdapter.CommonViewHolder>(MainItemDiffCallBack()) {
    companion object {
        const val HEADER = 1
        const val HEADER_WITH_MORE = 2
        const val DIVIDER = 3
        const val POPULAR_USER_LIST = 4
        const val USER_VIEW = 5
        const val ABOUT_TEEN_LIST = 6
        const val TOURNAMENT_LIST = 7
        const val LOADING_VIEW = 8
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            HEADER -> {
                val itemBinding = ItemMainHeaderBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.HeaderViewHolder(itemBinding)
            }

            HEADER_WITH_MORE -> {
                val itemBinding = ItemMainHeaderMoreBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.TournamentHeaderViewHolder(context, itemBinding, listener)
            }

            DIVIDER -> {
                val itemBinding = ItemDividerBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.DividerViewHolder(itemBinding)
            }

            POPULAR_USER_LIST -> {
                val itemBinding = ItemMainPopularTeenListviewBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.PopularUserListViewHolder(context, itemBinding, listener)
            }

            USER_VIEW -> {
                val itemBinding = ItemTeenBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.UserViewHolder(context, itemBinding, listener)
            }

            ABOUT_TEEN_LIST -> {
                val itemBinding =
                    ItemMainAboutTeenListviewBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.AboutTeenListViewHolder(itemBinding, listener)
            }

            TOURNAMENT_LIST -> {
                val itemBinding =
                    ItemMainTournamentListviewBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.TournamentListViewHolder(itemBinding, listener)
            }

            LOADING_VIEW -> {
                val itemBinding = ItemMainLoadingBinding.inflate(layoutInflater, parent, false)
                CommonViewHolder.LoadingViewHolder(itemBinding)
            }

            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        when (holder) {
            is CommonViewHolder.HeaderViewHolder -> {
                (getItem(position) as? MainItem.HeaderView)?.let { holder.bind(it) }
            }

            is CommonViewHolder.TournamentHeaderViewHolder -> {
                (getItem(position) as? MainItem.HeaderWithMoreView)?.let { holder.bind(it) }
            }

            is CommonViewHolder.PopularUserListViewHolder -> {
                (getItem(position) as? MainItem.UserListView)?.let { holder.bind(it) }
            }

            is CommonViewHolder.UserViewHolder -> {
                (getItem(position) as? MainItem.UserView)?.let { holder.bind(it, itemCount, position) }
            }

            is CommonViewHolder.AboutTeenListViewHolder -> {
                (getItem(position) as? MainItem.AboutTeenListView)?.let { holder.bind(it) }
            }

            is CommonViewHolder.TournamentListViewHolder -> {
                (getItem(position) as? MainItem.TournamentListView)?.let { holder.bind(it) }
            }

            is CommonViewHolder.DividerViewHolder -> {
                (getItem(position) as? MainItem.DividerView)?.let { holder.bind(it) }
            }

            is CommonViewHolder.LoadingViewHolder -> {
                (getItem(position) as? MainItem.LoadingView)?.let { holder.bind((it)) }
            }

            else -> throw IllegalArgumentException("Invalid ViewHolder")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MainItem.HeaderView -> HEADER
            is MainItem.HeaderWithMoreView -> HEADER_WITH_MORE
            is MainItem.DividerView -> DIVIDER
            is MainItem.UserListView -> POPULAR_USER_LIST
            is MainItem.AboutTeenListView -> ABOUT_TEEN_LIST
            is MainItem.TournamentListView -> TOURNAMENT_LIST
            is MainItem.UserView -> USER_VIEW
            is MainItem.LoadingView -> LOADING_VIEW
        }
    }

    sealed class CommonViewHolder(
        binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        class HeaderViewHolder(
            private val binding: ItemMainHeaderBinding
        ) : CommonViewHolder(binding) {
            fun bind(item: MainItem) {
                val headerView = item as? MainItem.HeaderView ?: return
                with(binding) {
                    tvHeader.text = headerView.text
                }
            }
        }

        class TournamentHeaderViewHolder(
            private val context: Context,
            private val binding: ItemMainHeaderMoreBinding,
            private val listener: MainAdapterListener
        ) : CommonViewHolder(binding) {
            fun bind(item: MainItem) {
                val headerMoreView = item as? MainItem.HeaderWithMoreView ?: return
                with(binding) {
                    tvHeader.text = headerMoreView.text

                    // 토너먼트 더 보기
                    tvMore.setOnClickListener {
                        if (context.getString(R.string.main_tournament_in_progress) == headerMoreView.text) {
                            listener.onTournamentMoreClicks()
                        }
                    }
                }
            }
        }

        class PopularUserListViewHolder(
            private val context: Context,
            val binding: ItemMainPopularTeenListviewBinding,
            private val listener: MainAdapterListener
        ) : CommonViewHolder(binding) {
            private val snapHelper = PagerSnapHelper()

            fun bind(item: MainItem) {
                val userListView = item as? MainItem.UserListView

                with(binding) {
                    // SnapHelper 설정
                    snapHelper.attachToRecyclerView(rvMainTeenPopularList)

                    val popularUserAdapter = PopularUserAdapter(context, listener)
                    with(rvMainTeenPopularList) {
                        adapter = popularUserAdapter

                        addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                when(newState) {
                                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                                        // 사용자가 스크롤 중
                                        listener.stopAutoScroll()
                                    }

                                    RecyclerView.SCROLL_STATE_IDLE -> {
                                        // 스크롤이 멈춘 후 자동 스크롤 재개
                                        val layoutManager = recyclerView.layoutManager
                                        val centerView = snapHelper.findSnapView(layoutManager)
                                        val pos = centerView?.let { layoutManager?.getPosition(it) }
                                        pos?.let {
                                            // 페이지가 변경될 때마다 이 위치(pos)를 사용하여 원하는 작업을 수행
                                            listener.saveUserPosition(it)
                                            listener.stopAutoScroll()
                                            listener.startAutoScroll()
                                        }
                                    }
                                }
                            }
                        })
                    }

                    popularUserAdapter.submitList(userListView?.userList) {
                        rvMainTeenPopularList.doOnLayout {
                            listener.scrollToPreviousUser()
                        }
                    }
                }
            }
        }

        class UserViewHolder(
            private val context: Context,
            private val binding: ItemTeenBinding,
            private val listener: MainAdapterListener
        ) : CommonViewHolder(binding) {
            fun bind(item: MainItem, itemCount: Int, position: Int) {
                val userView = item as? MainItem.UserView
                with(binding) {
                    userView?.let {
                        val user = it.user
                        val userName = "${user.userName}, ${user.userAge}"
                        tvSchool.text = user.userSchoolName
                        tvName.text = userName
                        Glide.with(context).load(user.userImage).into(imgTodayTeen) // 프로필 이미지
                        btnLike.isSelected = user.likeStatus            // 좋아요 버튼 Selector

                        imgTodayTeen.setOnClickListener {
                            listener.onUserClicks(user)
                        }
                        btnChat.setOnClickListener {
                            listener.onUserChatClicks(user)
                        }
                        btnLike.setOnClickListener {
                            listener.onUserLikeClicks(btnLike, user)
                        }
                        btnSetting.setOnClickListener {
                            listener.onUserMoreClicks(btnSetting, user)
                        }
                    }
                }
            }

            fun updateLikeBtn(isLike: Boolean) {
                binding.btnLike.isSelected = isLike
            }
        }

        class AboutTeenListViewHolder(
            private val binding: ItemMainAboutTeenListviewBinding,
            private val listener: MainAdapterListener
        ) : CommonViewHolder(binding) {
            fun bind(item: MainItem) {
                with(binding) {
                    val aboutTeenListView = item as? MainItem.AboutTeenListView

                    val adapter = AboutTeenAdapter(listener)
                    adapter.submitList(aboutTeenListView?.aboutTeenList)
                    rvItemMainAboutTeenList.adapter = adapter
                }
            }
        }

        class TournamentListViewHolder(
            private val binding: ItemMainTournamentListviewBinding,
            private val listener: MainAdapterListener
        ) : CommonViewHolder(binding) {
            fun bind(item: MainItem) {
                val tournamentListView = item as? MainItem.TournamentListView
                with(binding) {
                    val tournamentAdapter = TournamentAdapter(listener)
                    rvItemTournamentList.adapter = tournamentAdapter

                    tournamentListView?.tournamentList?.let {
                        tournamentAdapter.submitList(it)
                    }
                }
            }
        }

        class DividerViewHolder(
            private val binding: ItemDividerBinding
        ) : CommonViewHolder(binding) {
            fun bind(item: MainItem) {
                // ...
            }
        }

        class LoadingViewHolder(
            private val binding: ItemMainLoadingBinding
        ): CommonViewHolder(binding) {
            fun bind(item: MainItem) {
                // ...
            }
        }
    }

    fun updateView(list: List<MainItem>) {
        submitList(list)
    }

    fun addLoadingView(scrollToPosition: (Int) -> Unit) {
        val currentList = currentList.toMutableList()
        currentList.add(MainItem.LoadingView)

        submitList(currentList) {
            scrollToPosition(currentList.size - 1)
        }
    }

    fun removeLoadingView() {
        val currentList = currentList.toMutableList()
        if(currentList.lastIndex > 0) currentList.removeAt(currentList.lastIndex)

        submitList(currentList)
    }

    fun updateUserLikeStatus(recyclerView: RecyclerView, userId: Int, isLike: Boolean) {
        // 전체 아이템 중에서 해당 userId를 가진 유저 아이템 찾기
        val position = currentList.indexOfFirst { item ->
            when (item) {
                is MainItem.UserView -> item.user.userId == userId
                else -> false
            }
        }

        if (position != -1) {
            // 해당 위치의 ViewHolder 찾기
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as? CommonViewHolder.UserViewHolder

            // ViewHolder가 현재 화면에 보이는 경우 직접 업데이트
            viewHolder?.updateLikeBtn(isLike)

            // 데이터도 업데이트
            val currentItem = currentList[position] as? MainItem.UserView
            currentItem?.user?.likeStatus = isLike

            // 해당 포지션만 갱신
            notifyItemChanged(position)
        }
    }
}
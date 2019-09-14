package com.fastaccess.github.ui.modules.profile.followersandfollowings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.addKeyLineDivider
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.route
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.adapter.ProfileFollowingFollowersAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.profile.followersandfollowings.viewmodel.FollowersFollowingViewModel
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 15.10.18.
 */
class ProfileFollowersFragment : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FollowersFollowingViewModel::class.java) }
    private val loginBundle: String by lazy { arguments?.getString(EXTRA) ?: "" }
    private val isFollowers: Boolean by lazy { arguments?.getBoolean(EXTRA_TWO) ?: false }
    private val adapter by lazy { ProfileFollowingFollowersAdapter { url -> route(url) } }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        recyclerView.adapter = adapter
        recyclerView.addKeyLineDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadUsers(loginBundle, isFollowers, true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadUsers(loginBundle, isFollowers, true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadUsers(loginBundle, isFollowers) } }
        listenToChanges()
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.getUsers(loginBundle, isFollowers).observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(if (isFollowers) FragmentType.FOLLOWERS else FragmentType.FOLLOWINGS, it)
        }
    }

    companion object {
        fun newInstance(
            login: String,
            isFollowers: Boolean
        ) = ProfileFollowersFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA, login)
                putBoolean(EXTRA_TWO, isFollowers)
            }
        }
    }
}
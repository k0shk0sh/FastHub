package com.fastaccess.fasthub.commit.view.files

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.fasthub.commit.R
import com.fastaccess.fasthub.commit.adapter.CommitFilesAdapter
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.extensions.observeNotNull
import javax.inject.Inject

class CommitFilesFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sha by lazy { arguments?.getString(EXTRA) ?: throw NullPointerException("sha is null") }
    private val login by lazy { arguments?.getString(EXTRA_TWO) ?: throw NullPointerException("login is null") }
    private val repo by lazy { arguments?.getString(EXTRA_THREE) ?: throw NullPointerException("repo is null") }
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(CommitFilesViewModel::class.java) }
    private val adapter by lazy {
        CommitFilesAdapter { position, commitFilesModel ->
            // TODO(open file)
        }
    }

    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {

        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadFiles(login, repo, sha)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }

        if (savedInstanceState == null && viewModel.filesLiveData.value == null) {
            viewModel.loadFiles(login, repo, sha)
        }

        observeChanges()
    }

    private fun observeChanges() {
        viewModel.filesLiveData.observeNotNull(this) {
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.FILES, it)
        }
    }

    companion object {
        const val COMMENT_REQUEST_CODE = 1001
        const val EDIT_COMMENT_REQUEST_CODE = 1003

        fun newInstance(
            sha: String,
            login: String,
            repo: String
        ) = CommitFilesFragment().apply {
            arguments = bundleOf(
                EXTRA to sha,
                EXTRA_TWO to login,
                EXTRA_THREE to repo
            )
        }
    }
}
package com.fastaccess.fasthub.commit.view.files

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.fasthub.commit.R
import com.fastaccess.fasthub.commit.adapter.CommitFilesAdapter
import com.fastaccess.fasthub.diff.DiffViewerActivity
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_FOUR
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import javax.inject.Inject

class CommitFilesFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sha by lazy { arguments?.getString(EXTRA) ?: throw NullPointerException("sha is null") }
    private val login by lazy { arguments?.getString(EXTRA_TWO) ?: throw NullPointerException("login is null") }
    private val repo by lazy { arguments?.getString(EXTRA_THREE) ?: throw NullPointerException("repo is null") }
    private val number by lazy { arguments?.getInt(EXTRA_FOUR) ?: 0 }
    private val isPr by lazy { number > 0 }
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(CommitFilesViewModel::class.java) }
    private val adapter by lazy {
        CommitFilesAdapter { position, commitFilesModel ->
            DiffViewerActivity.startActivity(requireContext(), commitFilesModel.patch ?: "")
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
                if (isPr) {
                    viewModel.loadFiles(login, repo, number, true)
                } else {
                    viewModel.loadFiles(login, repo, sha)
                }
            } else {
                swipeRefresh.isRefreshing = false
            }
        }

        if (savedInstanceState == null || viewModel.filesLiveData.value == null) {
            if (isPr) {
                viewModel.loadFiles(login, repo, number, true)
            } else {
                viewModel.loadFiles(login, repo, sha)
            }
        }

        if (isPr) {
            recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadFiles(login, repo, number) } }
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
        fun newInstance(
            sha: String? = null,
            login: String,
            repo: String,
            number: Int = 0
        ) = CommitFilesFragment().apply {
            arguments = bundleOf(
                EXTRA to sha,
                EXTRA_TWO to login,
                EXTRA_THREE to repo,
                EXTRA_FOUR to number
            )
        }
    }
}
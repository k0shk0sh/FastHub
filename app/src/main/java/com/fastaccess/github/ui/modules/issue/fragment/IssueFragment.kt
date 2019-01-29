package com.fastaccess.github.ui.modules.issue.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.IssueTimelineAdapter
import com.fastaccess.github.ui.modules.issue.fragment.viewmodel.IssueTimelineViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_THREE
import com.fastaccess.github.utils.EXTRA_TWO
import com.fastaccess.github.utils.extensions.addDivider
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.issue_pr_fragment_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 28.01.19.
 */
class IssueFragment : BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(IssueTimelineViewModel::class.java) }
    private val login by lazy { arguments?.getString(EXTRA) ?: "" }
    private val repo by lazy { arguments?.getString(EXTRA_TWO) ?: "" }
    private val number by lazy { arguments?.getInt(EXTRA_THREE) ?: 0 }
    private val adapter by lazy { IssueTimelineAdapter() }

    override fun layoutRes(): Int = R.layout.issue_pr_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar("${getString(R.string.issue)}#$number")
        bottomBar.inflateMenu(R.menu.issue_menu)
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        recyclerView.addOnLoadMore { }
        if (savedInstanceState == null) {
            viewModel.loadIssue(login, repo, number)
        }
        swipeRefresh.setOnRefreshListener {
            viewModel.loadIssue(login, repo, number)
            viewModel.loadData(login, repo, number, true)
        }
        observeChanges()
    }

    private fun observeChanges() {
        viewModel.getIssue(login, repo, number).observeNotNull(this) {
            adapter.submitList(listOf(it))
        }
    }


    companion object {
        const val TAG = "IssueFragment"
        fun newInstance(login: String, repo: String, number: Int) = IssueFragment().apply {
            arguments = bundleOf(EXTRA to login, EXTRA_TWO to repo, EXTRA_THREE to number)
        }
    }
}
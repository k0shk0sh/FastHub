package com.fastaccess.github.ui.modules.issuesprs.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.ActivityType
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.ProfileFeedsAdapter
import com.fastaccess.github.ui.adapter.base.CurrentState
import com.fastaccess.github.ui.modules.issuesprs.filter.FilterIssuesPrsBottomSheet
import com.fastaccess.github.ui.modules.issuesprs.fragment.viewmodel.FilterIssuePullRequestsViewModel
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.addDivider
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.issues_prs_fragment_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FilterIssuePullRequestsFragment : BaseFragment(), FilterIssuesPrsBottomSheet.FilterIssuesPrsCallback {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FilterIssuePullRequestsViewModel::class.java) }
    private val adapter by lazy { ProfileFeedsAdapter() }
    private val fragmentType by lazy { arguments?.getSerializable(EXTRA) as? FragmentType }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.issues_prs_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(when (fragmentType) {
            FragmentType.FILTER_PRS -> R.string.pull_requests
            else -> R.string.issues
        })
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadFeeds(true)
        swipeRefresh.setOnRefreshListener {
            recyclerView.resetScrollState()
            viewModel.loadFeeds(true)
        }
        recyclerView.addOnLoadMore { viewModel.loadFeeds() }
        filter.setOnClickListener {
            val item = viewModel.filterModel.copy() // mutability!
            MultiPurposeBottomSheetDialog.show(childFragmentManager,
                MultiPurposeBottomSheetDialog.BottomSheetFragmentType.FILTER_ISSUES, item)
        }
        listenToChanges()
    }

    override fun onFilterApplied(model: FilterIssuesPrsModel) {
        viewModel.filter(model)
    }

    private fun listenToChanges() {
        viewModel.progress.observeNotNull(this) {
            adapter.currentState = if (it) CurrentState.LOADING else CurrentState.DONE
        }

        viewModel.feeds().observeNotNull(this) {
            adapter.currentState = CurrentState.DONE
            adapter.submitList(it)
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.FEEDS, it)
        }
    }

    companion object {
        fun newInstance(activityType: ActivityType) = FilterIssuePullRequestsFragment().apply {
            arguments = bundleOf(EXTRA to when (activityType) {
                ActivityType.FILTER_PR -> FragmentType.FILTER_PRS
                else -> FragmentType.FILTER_ISSUES
            })
        }
    }
}


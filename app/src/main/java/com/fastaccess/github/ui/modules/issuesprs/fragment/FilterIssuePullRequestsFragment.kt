package com.fastaccess.github.ui.modules.issuesprs.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.data.model.ActivityType
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.route
import com.fastaccess.github.ui.adapter.MyIssuesPrsAdapter
import com.fastaccess.github.ui.modules.issuesprs.filter.FilterIssuesPrsBottomSheet
import com.fastaccess.github.ui.modules.issuesprs.fragment.viewmodel.FilterIssuePullRequestsViewModel
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import kotlinx.android.synthetic.main.issues_prs_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class FilterIssuePullRequestsFragment : com.fastaccess.github.base.BaseFragment(), FilterIssuesPrsBottomSheet.FilterIssuesPrsCallback {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FilterIssuePullRequestsViewModel::class.java) }
    private val adapter by lazy {
        MyIssuesPrsAdapter {
            route("${it.url}")
        }
    }
    private val fragmentType by lazy { arguments?.getSerializable(EXTRA) as? FragmentType }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.issues_prs_fragment_layout

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        setupToolbar(
            when (fragmentType) {
                FragmentType.FILTER_PRS -> R.string.pull_requests
                else -> R.string.issues
            }
        )

        viewModel.isPr = fragmentType == FragmentType.FILTER_PRS
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, appBar)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.loadData(true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadData(true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadData() } }
        filter.setOnClickListener {
            val item = viewModel.filterModel.copy() // mutability!
            MultiPurposeBottomSheetDialog.show(
                childFragmentManager,
                MultiPurposeBottomSheetDialog.BottomSheetFragmentType.FILTER_ISSUES, item
            )
        }
        listenToChanges()
    }

    override fun onFilterApplied(model: FilterIssuesPrsModel) {
        viewModel.filter(model)
    }

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance(activityType: ActivityType) = FilterIssuePullRequestsFragment().apply {
            arguments = bundleOf(
                EXTRA to when (activityType) {
                    ActivityType.FILTER_PR -> FragmentType.FILTER_PRS
                    else -> FragmentType.FILTER_ISSUES
                }
            )
        }
    }
}
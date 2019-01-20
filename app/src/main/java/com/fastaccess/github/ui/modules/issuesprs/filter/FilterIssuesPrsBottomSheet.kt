package com.fastaccess.github.ui.modules.issuesprs.filter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.utils.EXTRA
import kotlinx.android.synthetic.main.filter_issue_pr_layout.*

/**
 * Created by Kosh on 19.01.19.
 */
class FilterIssuesPrsBottomSheet : BaseFragment() {

    private val model by lazy { (arguments?.getParcelable(EXTRA) as? FilterIssuesPrsModel) ?: FilterIssuesPrsModel() }
    private var callback: FilterIssuesPrsCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is FilterIssuesPrsCallback -> context
            parentFragment is FilterIssuesPrsCallback -> parentFragment as FilterIssuesPrsCallback
            parentFragment?.parentFragment is FilterIssuesPrsCallback -> parentFragment?.parentFragment as FilterIssuesPrsCallback // deep hierarchy
            else -> null
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.filter_issue_pr_layout
    override fun viewModel(): BaseViewModel? = null
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.filter)
        reviewRequest.isVisible = model.isPr

        if (savedInstanceState == null) {
            initState()
        }

        assignCheckListener()

        submit.setOnClickListener {
            callback?.onFilterApplied(model)
            (parentFragment as? BaseBottomSheetDialogFragment)?.dismiss()
        }
    }

    private fun initState() {
        filter.check(when (model.searchBy) {
            FilterIssuesPrsModel.SearchBy.CREATED -> R.id.created
            FilterIssuesPrsModel.SearchBy.ASSIGNED -> R.id.assigned
            FilterIssuesPrsModel.SearchBy.MENTIONED -> R.id.mentioned
            FilterIssuesPrsModel.SearchBy.REVIEW_REQUESTS -> R.id.reviewRequest
        })
        type.check(when (model.searchType) {
            FilterIssuesPrsModel.SearchType.OPEN -> R.id.open
            FilterIssuesPrsModel.SearchType.CLOSED -> R.id.closed
        })
        visibility.check(when (model.searchVisibility) {
            FilterIssuesPrsModel.SearchVisibility.BOTH -> R.id.bothVisibility
            FilterIssuesPrsModel.SearchVisibility.PUBLIC -> R.id.publicRepos
            FilterIssuesPrsModel.SearchVisibility.PRIVATE -> R.id.privateRepos
        })
        sort.check(when (model.searchSortBy) {
            FilterIssuesPrsModel.SearchSortBy.NEWEST -> R.id.newest
            FilterIssuesPrsModel.SearchSortBy.OLDEST -> R.id.oldest
            FilterIssuesPrsModel.SearchSortBy.MOST_COMMENTED -> R.id.mostCommented
            FilterIssuesPrsModel.SearchSortBy.LEAST_COMMENTED -> R.id.leastCommented
            FilterIssuesPrsModel.SearchSortBy.RECENTLY_UPDATED -> R.id.recentlyUpdated
            FilterIssuesPrsModel.SearchSortBy.LEAST_RECENTLY_UPDATED -> R.id.leastRecentlyUpdated
        })
    }

    private fun assignCheckListener() {
        filter.setOnCheckedChangeListener { group, id ->
            when (id) {
                R.id.created -> model.searchBy = FilterIssuesPrsModel.SearchBy.CREATED
                R.id.assigned -> model.searchBy = FilterIssuesPrsModel.SearchBy.ASSIGNED
                R.id.mentioned -> model.searchBy = FilterIssuesPrsModel.SearchBy.MENTIONED
                R.id.reviewRequest -> model.searchBy = FilterIssuesPrsModel.SearchBy.REVIEW_REQUESTS
            }
        }
        type.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.open -> model.searchType = FilterIssuesPrsModel.SearchType.OPEN
                R.id.closed -> model.searchType = FilterIssuesPrsModel.SearchType.CLOSED
            }
        }
        visibility.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.bothVisibility -> model.searchVisibility = FilterIssuesPrsModel.SearchVisibility.BOTH
                R.id.privateRepos -> model.searchVisibility = FilterIssuesPrsModel.SearchVisibility.PRIVATE
                R.id.publicRepos -> model.searchVisibility = FilterIssuesPrsModel.SearchVisibility.PUBLIC
            }
        }
        sort.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.newest -> model.searchSortBy = FilterIssuesPrsModel.SearchSortBy.NEWEST
                R.id.oldest -> model.searchSortBy = FilterIssuesPrsModel.SearchSortBy.OLDEST
                R.id.mostCommented -> model.searchSortBy = FilterIssuesPrsModel.SearchSortBy.MOST_COMMENTED
                R.id.leastCommented -> model.searchSortBy = FilterIssuesPrsModel.SearchSortBy.LEAST_COMMENTED
                R.id.recentlyUpdated -> model.searchSortBy = FilterIssuesPrsModel.SearchSortBy.RECENTLY_UPDATED
                R.id.leastRecentlyUpdated -> model.searchSortBy = FilterIssuesPrsModel.SearchSortBy.LEAST_RECENTLY_UPDATED
            }
        }
    }

    companion object {
        fun newInstance(model: FilterIssuesPrsModel) = FilterIssuesPrsBottomSheet().apply {
            arguments = bundleOf(EXTRA to model)
        }
    }

    interface FilterIssuesPrsCallback {
        fun onFilterApplied(model: FilterIssuesPrsModel)
    }
}
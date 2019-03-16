package com.fastaccess.github.ui.modules.search.filter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.evernote.android.state.State
import com.fastaccess.data.model.parcelable.FilterByRepo
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.utils.EXTRA
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.filter_search_layout.*

/**
 * Created by Kosh on 19.01.19.
 */
class FilterSearchBottomSheet : BaseFragment() {

    @State lateinit var model: FilterSearchModel
    private var callback: FilterSearchCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is FilterSearchCallback -> context
            parentFragment is FilterSearchCallback -> parentFragment as FilterSearchCallback
            parentFragment?.parentFragment is FilterSearchCallback -> parentFragment?.parentFragment as FilterSearchCallback // deep hierarchy
            else -> null
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.filter_search_layout
    override fun viewModel(): BaseViewModel? = null
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            model = (arguments?.getParcelable(EXTRA) as? FilterSearchModel) ?: FilterSearchModel()
        }
        setupToolbar(R.string.filter)
        submit.setOnClickListener {
            model.filterByRepo.name = limitByEditText.text?.toString()
            model.filterByRepo.language = languageEditText.text?.toString()
            callback?.onFilterApplied(model)
            (parentFragment as? BaseBottomSheetDialogFragment)?.dismiss()
        }
        initState()
    }

    private fun initState() {
        when (model.searchBy) {
            FilterSearchModel.SearchBy.REPOS -> {
                filterIssuesPr.isVisible = false
                filterRepos.isVisible = true
                initRepoCheckState()
                initRepoCheckListener()
            }
            FilterSearchModel.SearchBy.ISSUES -> {
                filterRepos.isVisible = false
                filterIssuesPr.isVisible = true
                reviewRequest.isVisible = false
                searchType.check(R.id.issues)
                initIssuePrCheckState()
                initIssuesPrsCheckListener()
            }
            FilterSearchModel.SearchBy.PRS -> {
                filterRepos.isVisible = false
                filterIssuesPr.isVisible = true
                reviewRequest.isVisible = true
                searchType.check(R.id.prs)
                initIssuePrCheckState()
                initIssuesPrsCheckListener()
            }
            FilterSearchModel.SearchBy.USERS -> {
                searchType.check(R.id.users)
                filterIssuesPr.isVisible = false
                filterRepos.isVisible = false
            }
            FilterSearchModel.SearchBy.NONE -> {
                filterIssuesPr.isVisible = false
                filterRepos.isVisible = false
            }
        }

        searchType.setOnCheckedChangeListener { _, id ->
            when (id) {
                -1 -> model = FilterSearchModel()
                R.id.repos -> {
                    filterIssuesPr.isVisible = false
                    filterRepos.isVisible = true
                    model.searchBy = FilterSearchModel.SearchBy.REPOS
                    initRepoCheckListener()
                }
                R.id.prs, R.id.issues -> {
                    filterIssuesPr.isVisible = true
                    reviewRequest.isVisible = false
                    model.searchBy = FilterSearchModel.SearchBy.ISSUES
                    (id == R.id.prs).isTrue {
                        model.searchBy = FilterSearchModel.SearchBy.PRS
                        model.filterIssuesPrsModel.isPr = true
                        reviewRequest.isVisible = true
                    }
                    filterRepos.isVisible = false
                    initIssuesPrsCheckListener()
                }
                R.id.users -> {
                    model.searchBy = FilterSearchModel.SearchBy.USERS
                    filterIssuesPr.isVisible = false
                    filterRepos.isVisible = false
                }
            }
        }
    }

    private fun initRepoCheckState() {
        searchType.check(R.id.repos)
        model.filterByRepo.let { model ->
            searchIn.check(when (model.filterByRepoIn) {
                FilterByRepo.FilterByRepoIn.ALL -> R.id.all
                FilterByRepo.FilterByRepoIn.NAME -> R.id.name
                FilterByRepo.FilterByRepoIn.DESCRIPTION -> R.id.description
                FilterByRepo.FilterByRepoIn.README -> R.id.readme
            })
            model.filterByRepoLimitBy?.let { limit ->
                limitBy.check(when (limit) {
                    FilterByRepo.FilterByRepoLimitBy.USERNAME -> R.id.username
                    FilterByRepo.FilterByRepoLimitBy.ORG -> R.id.org
                })
                limitByText.isVisible = true
            }
            model.filterByRepoSortBy?.let { limit ->
                sortBy.check(when (limit) {
                    FilterByRepo.FilterByRepoSortBy.MOST_STARS -> R.id.mostStars
                    FilterByRepo.FilterByRepoSortBy.LEAST_STARS -> R.id.leastStars
                })

            }
            limitByEditText.setText(model.name ?: "")
            languageEditText.setText(model.language ?: "")
        }
    }

    private fun initIssuePrCheckState() {
        model.filterIssuesPrsModel.let { model ->
            reviewRequest.isVisible = model.isPr == true
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
    }

    private fun initRepoCheckListener() {
        searchIn.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.all -> model.filterByRepo.filterByRepoIn = FilterByRepo.FilterByRepoIn.ALL
                R.id.name -> model.filterByRepo.filterByRepoIn = FilterByRepo.FilterByRepoIn.NAME
                R.id.description -> model.filterByRepo.filterByRepoIn = FilterByRepo.FilterByRepoIn.DESCRIPTION
                R.id.readme -> model.filterByRepo.filterByRepoIn = FilterByRepo.FilterByRepoIn.README
            }
        }
        limitBy.setOnCheckedChangeListener { group, id ->
            when (id) {
                R.id.username -> model.filterByRepo.filterByRepoLimitBy = FilterByRepo.FilterByRepoLimitBy.USERNAME
                R.id.org -> model.filterByRepo.filterByRepoLimitBy = FilterByRepo.FilterByRepoLimitBy.ORG
                -1 -> {
                    model.filterByRepo.filterByRepoLimitBy = null
                    limitByText.editText?.setText("")
                }
            }
            limitByText.isVisible = group.checkedChipId != -1
        }
        sortBy.setOnCheckedChangeListener { group, id ->
            when (id) {
                R.id.mostStars -> model.filterByRepo.filterByRepoSortBy = FilterByRepo.FilterByRepoSortBy.MOST_STARS
                R.id.leastStars -> model.filterByRepo.filterByRepoSortBy = FilterByRepo.FilterByRepoSortBy.LEAST_STARS
            }

        }
        languages.setOnCheckedChangeListener { chipGroup, id ->
            if (id != -1) {
                languageEditText.setText(chipGroup.findViewById<Chip>(id).text)
            } else {
                languageEditText.setText("")
            }
        }
    }

    private fun initIssuesPrsCheckListener() {
        model.filterIssuesPrsModel.let { model ->
            filter.setOnCheckedChangeListener { _, id ->
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
    }

    companion object {
        fun newInstance(model: FilterSearchModel) = FilterSearchBottomSheet().apply {
            arguments = bundleOf(EXTRA to model)
        }
    }

    interface FilterSearchCallback {
        fun onFilterApplied(model: FilterSearchModel)
    }
}
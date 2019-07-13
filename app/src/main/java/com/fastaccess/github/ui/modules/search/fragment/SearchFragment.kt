package com.fastaccess.github.ui.modules.search.fragment

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.data.persistence.models.SuggestionsModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.observeNull
import com.fastaccess.github.ui.adapter.MyIssuesPrsAdapter
import com.fastaccess.github.ui.adapter.SearchReposAdapter
import com.fastaccess.github.ui.adapter.ShortUsersAdapter
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog.BottomSheetFragmentType.FILTER_SEARCH
import com.fastaccess.github.ui.modules.search.filter.FilterSearchBottomSheet
import com.fastaccess.github.ui.modules.search.fragment.viewmodel.FilterSearchViewModel
import com.fastaccess.github.utils.extensions.*
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.fab_simple_refresh_list_layout.*
import kotlinx.android.synthetic.main.search_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 20.01.19.
 */
class SearchFragment : BaseFragment(), FilterSearchBottomSheet.FilterSearchCallback {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(FilterSearchViewModel::class.java) }
    private val issuesPrsAdapter by lazy {
        MyIssuesPrsAdapter {
            route(it.url)
        }
    }
    private val reposAdapter by lazy { SearchReposAdapter() }
    private val usersAdapter by lazy {
        ShortUsersAdapter { url ->
            requireContext().route(url)
        }
    }

    override fun layoutRes(): Int = R.layout.search_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
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
        backBtn.setOnClickListener { activity?.onBackPressed() }
        clear.setOnClickListener { searchEditText.setText("") }
        filter.setOnClickListener {
            val model = viewModel.filterModel.copy()
            MultiPurposeBottomSheetDialog.show(childFragmentManager, FILTER_SEARCH, model)
        }

        searchEditText.setAdapter(ArrayAdapter<SuggestionsModel>(requireContext(), android.R.layout.simple_list_item_1))

        searchEditText.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_GO) {
                v.hideKeyboard()
                if (viewModel.filterModel.searchBy == FilterSearchModel.SearchBy.NONE) {
                    filter.callOnClick()
                } else {
                    onFilterApplied(viewModel.filterModel)
                }
                true
            } else {
                false
            }
        }
        searchEditText.doAfterTextChanged { text ->
            val show = !text.isNullOrEmpty()
            if (show != clear.isVisible) { // prevent multiple hide/show
                clear.isVisible = show
            }
            searchSuggestions(text)
        }

        listenToChanges()
    }

    override fun onFilterApplied(model: FilterSearchModel) {
        if (searchEditText.asString().isEmpty()) {
            viewModel.filterModel = model
            searchEditText.error = getString(R.string.required_field)
        } else {
            model.searchQuery = searchEditText.asString()
            searchEditText.error = null
            isConnected().isTrue { viewModel.filter(model) }
        }
    }

    private fun listenToChanges() {
        viewModel.issuesPrsData.observeNotNull(this) {
            if (recyclerView.adapter !is MyIssuesPrsAdapter) {
                recyclerView.adapter = issuesPrsAdapter
            }
            (recyclerView.adapter as? MyIssuesPrsAdapter)?.submitList(it)
        }
        viewModel.reposData.observeNotNull(this) {
            if (recyclerView.adapter !is SearchReposAdapter) {
                recyclerView.adapter = reposAdapter
            }
            (recyclerView.adapter as? SearchReposAdapter)?.submitList(it)
        }
        viewModel.usersData.observeNotNull(this) {
            if (recyclerView.adapter !is ShortUsersAdapter) {
                recyclerView.adapter = usersAdapter
            }
            (recyclerView.adapter as? ShortUsersAdapter)?.submitList(it)
        }
    }

    private fun searchSuggestions(text: Editable?) {
        if (!text.isNullOrEmpty()) {
            viewModel.querySuggestion(text.toString()).observeNull(this) {
                val adapter = searchEditText?.adapter as? ArrayAdapter<SuggestionsModel> ?: return@observeNull
                adapter.clear()
                it?.let { list -> adapter.addAll(list) }
            }
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
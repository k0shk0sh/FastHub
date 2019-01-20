package com.fastaccess.github.ui.modules.search.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.MyIssuesPrsAdapter
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog.BottomSheetFragmentType.FILTER_SEARCH
import com.fastaccess.github.ui.modules.search.filter.FilterSearchBottomSheet
import com.fastaccess.github.ui.modules.search.fragment.viewmodel.FilterSearchViewModel
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.asString
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
    private val adapter by lazy { MyIssuesPrsAdapter() }

    override fun layoutRes(): Int = R.layout.search_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) viewModel.loadData(true)
        swipeRefresh.setOnRefreshListener {
            recyclerView.resetScrollState()
            viewModel.loadData(true)
        }
        recyclerView.addOnLoadMore { viewModel.loadData() }
        clear.setOnClickListener { searchEditText.setText("") }
        filter.setOnClickListener {
            val model = viewModel.filterModel.copy()
            MultiPurposeBottomSheetDialog.show(childFragmentManager, FILTER_SEARCH, model)
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_GO) {
                onFilterApplied(viewModel.filterModel)
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
        }

        listenToChanges()
    }

    override fun onFilterApplied(model: FilterSearchModel) {
        if (searchEditText.asString().isEmpty()) {
            searchEditText.error = getString(R.string.required_field)
        } else {
            model.searchQuery = searchEditText.asString()
            searchEditText.error = null
            viewModel.filter(model)
        }
    }

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
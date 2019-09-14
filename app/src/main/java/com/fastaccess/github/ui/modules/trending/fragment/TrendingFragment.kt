package com.fastaccess.github.ui.modules.trending.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.parcelable.FilterTrendingModel
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.addDivider
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.adapter.TrendingsAdapter
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.modules.trending.filter.FilterTrendingBottomSheet
import com.fastaccess.github.ui.modules.trending.fragment.viewmodel.TrendingViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fab_simple_refresh_list_layout.*
import kotlinx.android.synthetic.main.trending_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 23.01.19.
 */
class TrendingFragment : com.fastaccess.github.base.BaseFragment(), FilterTrendingBottomSheet.FilterTrendingCallback {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var gson: Gson

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(TrendingViewModel::class.java) }
    private val adapter by lazy { TrendingsAdapter() }

    override fun layoutRes(): Int = R.layout.trending_fragment_layout
    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.trending)
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, appBar)
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.load(viewModel.filterTrendingModel)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        filterTrending.setOnClickListener {
            val modelCopy = viewModel.filterTrendingModel.copy()
            MultiPurposeBottomSheetDialog.show(childFragmentManager,
                MultiPurposeBottomSheetDialog.BottomSheetFragmentType.TRENDING, modelCopy)
        }
        listenToChanges()
        if (savedInstanceState == null) {
            val model = arguments?.getParcelable(EXTRA) ?: FilterTrendingModel()
            isConnected().isTrue { viewModel.load(model) }
        }
    }

    override fun onFilterApplied(model: FilterTrendingModel) {
        viewModel.filterTrendingModel = model
        isConnected().isTrue { viewModel.load(model) }
    }

    private fun listenToChanges() {
        viewModel.trendingLiveData.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance(lan: String? = null, since: String? = null) = TrendingFragment().apply {
            arguments = bundleOf(EXTRA to FilterTrendingModel(lan ?: "", FilterTrendingModel.TrendingSince.getSince(since)))
        }
    }
}
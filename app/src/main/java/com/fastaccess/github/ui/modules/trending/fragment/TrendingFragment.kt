package com.fastaccess.github.ui.modules.trending.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.LanguageColorsModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.TrendingsAdapter
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.modules.trending.filter.FilterTrendingBottomSheet
import com.fastaccess.github.ui.modules.trending.fragment.viewmodel.TrendingViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_TWO
import com.fastaccess.github.utils.extensions.addDivider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.fab_simple_refresh_list_layout.*
import kotlinx.android.synthetic.main.trending_fragment_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 23.01.19.
 */
class TrendingFragment : BaseFragment(), FilterTrendingBottomSheet.FilterTrendingCallback {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var gson: Gson

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(TrendingViewModel::class.java) }
    private val adapter by lazy { TrendingsAdapter() }

    override fun layoutRes(): Int = R.layout.trending_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.trending)
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        swipeRefresh.setOnRefreshListener {
            recyclerView.resetScrollState()
            viewModel.load(viewModel.lang, viewModel.since)
        }
        filterTrending.setOnClickListener {
            MultiPurposeBottomSheetDialog.show(childFragmentManager,
                MultiPurposeBottomSheetDialog.BottomSheetFragmentType.TRENDING)
        }
        listenToChanges()
    }

    override fun onFilterApplied(lan: String, since: String) {
        viewModel.load(lan, since)
    }

    private fun listenToChanges() {
        viewModel.trendingLiveData.observeNotNull(this) {
            Timber.e("$it")
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance(lan: String? = null, since: String? = null) = TrendingFragment().apply {
            arguments = bundleOf().apply {
                if (!lan.isNullOrEmpty()) {
                    putString(EXTRA, lan)
                    putString(EXTRA_TWO, since ?: "daily")
                }
            }
        }
    }
}
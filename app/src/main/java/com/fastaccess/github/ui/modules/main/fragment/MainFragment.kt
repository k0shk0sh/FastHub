package com.fastaccess.github.ui.modules.main.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.ui.modules.adapter.MainIssuesAdapter
import com.fastaccess.github.ui.modules.adapter.MainPullRequestsAdapter
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.appbar_center_title_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainFragmentViewModel

    override fun layoutRes(): Int = R.layout.main_fragment_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainFragmentViewModel::class.java)
    }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        viewModel.load()

        swipeRefresh.setOnRefreshListener { viewModel.load() }
        toolbarTitle.setText(R.string.app_name)
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            if (offset == appBarLayout.totalScrollRange) {
                bottomBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            } else {
                bottomBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            }
        })

        listenToDataChanges()
    }

    private fun listenToDataChanges() {
        viewModel.progress.observe(this, Observer {
            swipeRefresh.isRefreshing = it == true
        })

        viewModel.prNode.observe(this, Observer {
            if (it == null) {
                pullRequestsList.removeAllCells()
            } else {
                it.filterNotNull().forEach { node -> pullRequestsList.addCell(MainPullRequestsAdapter(node)) }
            }
        })

        viewModel.issuesNode.observe(this, Observer {
            if (it == null) {
                issuesList.removeAllCells()
            } else {
                it.filterNotNull().forEach { node -> issuesList.addCell(MainIssuesAdapter(node)) }
            }
        })

        viewModel.error.observe(this, Observer {
            it?.let {
                view?.let { view ->
                    showSnackBar(view, resId = it.resId, message = it.message)
                }
            }
        })
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
package com.fastaccess.github.ui.modules.main.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.fastaccess.github.utils.extensions.observeNotNull
import kotlinx.android.synthetic.main.appbar_center_title_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*
import timber.log.Timber
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
        if (savedInstanceState == null) {
            viewModel.load()
        }
        swipeRefresh.setOnRefreshListener { viewModel.load() }
        toolbarTitle.setText(R.string.app_name)
        listenToDataChanges()
    }

    private fun listenToDataChanges() {
        viewModel.progress.observeNotNull(this, {
            swipeRefresh.isRefreshing = it == true
        })
        viewModel.notifications.observeNotNull(this, {
            Timber.e("${it.size}")
            notificationsList.removeAllCells()
            notificationsList.addCells(it)
        })
        viewModel.issues.observeNotNull(this, {
            issuesList.removeAllCells()
            issuesList.addCells(it)
        })
        viewModel.prs.observeNotNull(this, {
            pullRequestsList.removeAllCells()
            pullRequestsList.addCells(it)
        })
        viewModel.error.observeNotNull(this, {
            view?.let { view -> showSnackBar(view, resId = it.resId, message = it.message) }
        })
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
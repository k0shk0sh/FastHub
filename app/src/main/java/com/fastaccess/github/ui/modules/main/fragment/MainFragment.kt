package com.fastaccess.github.ui.modules.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.ui.adapter.FeedsAdapter
import com.fastaccess.github.ui.adapter.MainIssuesPrsAdapter
import com.fastaccess.github.ui.adapter.NotificationsAdapter
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.fastaccess.github.utils.extensions.observeNotNull
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.appbar_profile_title_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(MainFragmentViewModel::class.java) }
    private val issuesAdapter by lazy { MainIssuesPrsAdapter() }
    private val prsAdapter by lazy { MainIssuesPrsAdapter() }
    private val notificationAdapter by lazy { NotificationsAdapter() }
    private val feedsAdapter by lazy { FeedsAdapter() }

    override fun layoutRes(): Int = R.layout.main_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.load()
        }
        profile.isVisible = false
        swipeRefresh.setOnRefreshListener { viewModel.load() }
        toolbarTitle.setText(R.string.app_name)
        notificationsList.addDivider()
        issuesList.addDivider()
        pullRequestsList.addDivider()
        feedsList.addDivider()
        notificationsList.adapter = notificationAdapter
        issuesList.adapter = issuesAdapter
        pullRequestsList.adapter = prsAdapter
        feedsList.adapter = feedsAdapter
        bottomBar.inflateMenu(R.menu.main_bottom_bar_menu)
        bottomBar.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener true
        }
        val behaviour = BottomSheetBehavior.from(bottomSheet)
        bottomBar.setNavigationOnClickListener {
            behaviour.apply {
                state = if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) = Unit

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(p0: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> bottomBar.navigationIcon = ContextCompat.getDrawable(p0.context, R.drawable.ic_arrow_drop_down)
                    BottomSheetBehavior.STATE_COLLAPSED -> bottomBar.navigationIcon = ContextCompat.getDrawable(p0.context, R.drawable.ic_menu)
                }
            }
        })
        listenToDataChanges()
    }

    private fun listenToDataChanges() {
        viewModel.progress.observeNotNull(this) {
            swipeRefresh.isRefreshing = it == true
        }

        viewModel.feeds.observeNotNull(this) {
            feedsLayout.isVisible = it.isNotEmpty()
            feedsAdapter.submitList(it)
        }

        viewModel.notifications.observeNotNull(this) {
            notificationLayout.isVisible = it.isNotEmpty()
            notificationAdapter.submitList(it)
        }
        viewModel.issues.observeNotNull(this) {
            issuesLayout.isVisible = it.isNotEmpty()
            issuesAdapter.submitList(it)
        }
        viewModel.prs.observeNotNull(this) {
            pullRequestsLayout.isVisible = it.isNotEmpty()
            prsAdapter.submitList(it)
        }

        viewModel.error.observeNotNull(this) {
            view?.let { view -> showSnackBar(view, resId = it.resId, message = it.message) }
        }
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
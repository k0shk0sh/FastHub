package com.fastaccess.github.ui.modules.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.adapter.FeedsCell
import com.fastaccess.github.ui.adapter.MainIssuesPrsCell
import com.fastaccess.github.ui.adapter.NotificationsCell
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.fastaccess.github.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.appbar_profile_title_layout.*
import kotlinx.android.synthetic.main.bottm_bar_menu_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var preference: FastHubSharedPreference
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(MainFragmentViewModel::class.java) }

    override fun layoutRes(): Int = R.layout.main_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

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
        bottomBar.inflateMenu(R.menu.main_bottom_bar_menu)
        bottomBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.profile -> addDisposal(viewModel.login.subscribe({ route(it?.htmlUrl) }, ::print))
            }
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
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        shadow?.isVisible = false
                        bottomBar.navigationIcon = ContextCompat.getDrawable(p0.context, R.drawable.ic_arrow_drop_down)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        shadow?.isVisible = true
                        bottomBar.navigationIcon = ContextCompat.getDrawable(p0.context, R.drawable.ic_menu)
                    }
                }
            }
        })
        navigationView.setNavigationItemSelectedListener {
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            when (it.itemId) {
                R.id.logout -> viewModel.logout()
            }
            return@setNavigationItemSelectedListener true
        }
        listenToDataChanges()
        initClicks()
    }

    private fun initClicks() {
        starred.setOnClickListener { view ->
            addDisposal(viewModel.login
                    .subscribe({ route("${it?.htmlUrl}/${FragmentType.STARRED.tabName}") }, ::print))
        }
        repos.setOnClickListener { view ->
            addDisposal(viewModel.login
                    .subscribe({ route("${it?.htmlUrl}/${FragmentType.REPOS.tabName}") }, ::print))
        }
        gists.setOnClickListener { view ->
            addDisposal(viewModel.login
                    .subscribe({ route("${it?.htmlUrl}/${FragmentType.GISTS.tabName}") }, ::print))
        }
    }

    private fun listenToDataChanges() {
        viewModel.progress.observeNotNull(this) {
            swipeRefresh.isRefreshing = it == true
        }
        viewModel.feeds.observeNotNull(this) { list ->
            feedsLayout.isVisible = list.isNotEmpty()
            feedsList.addOrUpdateCells(list.asSequence().map { feedModel ->
                FeedsCell(feedModel).apply {
                    setOnCellClickListener {
                        route(it.actor?.url)
                    }
                }
            }.toList())
        }
        viewModel.notifications.observeNotNull(this) { list ->
            notificationLayout.isVisible = list.isNotEmpty()
            notificationsList.addOrUpdateCells(list.asSequence().map { NotificationsCell(it) }.toList())
        }
        viewModel.issues.observeNotNull(this) { list ->
            issuesLayout.isVisible = list.isNotEmpty()
            issuesList.addOrUpdateCells(list.asSequence().map { MainIssuesPrsCell(it) }.toList())
        }
        viewModel.prs.observeNotNull(this) { list ->
            pullRequestsLayout.isVisible = list.isNotEmpty()
            pullRequestsList.addOrUpdateCells(list.asSequence().map { MainIssuesPrsCell(it) }.toList())
        }
        viewModel.logoutProcess.observeNotNull(this) {
            if (it) {
                preference.token = null
                preference.otpCode = null
                activity?.finish()
            }
        }
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
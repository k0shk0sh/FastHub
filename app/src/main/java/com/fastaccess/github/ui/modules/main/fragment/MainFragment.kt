package com.fastaccess.github.ui.modules.main.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.getDrawable
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.platform.extension.onClick
import com.fastaccess.github.ui.adapter.MainScreenAdapter
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.ui.widget.dialog.IconDialogFragment
import com.fastaccess.github.utils.NOTIFICATION_LINK
import com.fastaccess.github.utils.SEARCH_LINK
import com.fastaccess.github.utils.TRENDING_LINK
import com.fastaccess.github.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.appbar_logo_center_title_layout.*
import kotlinx.android.synthetic.main.bottm_bar_menu_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment(), IconDialogFragment.IconDialogClickListener {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var preference: FastHubSharedPreference
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(MainFragmentViewModel::class.java) }
    private val behaviour by lazy { BottomSheetBehavior.from(bottomSheet) }
    private val adapter by lazy { MainScreenAdapter(onClickListener()) }

    override fun layoutRes(): Int = R.layout.main_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        if (savedInstanceState == null) {
            isConnected().isTrue { viewModel.load() }
        }
        setupToolbar(R.string.app_name)
        toolbar.navigationIcon = null
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                viewModel.load()
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.adapter = adapter
        bottomBar.inflateMenu(R.menu.main_bottom_bar_menu)
        behaviour.setBottomSheetCallback({ state: Int ->
            when (state) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    shadow?.isVisible = false
                    bottomBar.navigationIcon = requireContext().getDrawableCompat(R.drawable.ic_arrow_drop_down)
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    shadow?.isVisible = true
                    bottomBar.navigationIcon = requireContext().getDrawableCompat(R.drawable.ic_menu)
                }
            }
        })
        listenToDataChanges()
        initClicks()
    }

    override fun onBackPressed(): Boolean {
        return if (behaviour.state == BottomSheetBehavior.STATE_COLLAPSED) {
            true
        } else {
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            false
        }
    }

    override fun onClick(positive: Boolean) {
        positive.isTrue { viewModel.logout() }
    }

    private fun initClicks() {
        bottomBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.profile -> addDisposal(viewModel.login.subscribe({ route(it?.htmlUrl) }, ::print))
                R.id.notifications -> route(NOTIFICATION_LINK)
                R.id.search -> route(SEARCH_LINK)
            }
            return@setOnMenuItemClickListener true
        }
        navigationView.setNavigationItemSelectedListener {
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            when (it.itemId) {
                R.id.logout -> IconDialogFragment.show(
                    childFragmentManager, R.drawable.ic_info_outline, getString(R.string.logout),
                    getString(R.string.confirm_message), getString(R.string.logout), getString(R.string.cancel)
                )
                R.id.add_account -> LoginChooserActivity.startActivity(requireActivity(), false)
            }
            return@setNavigationItemSelectedListener true
        }
        bottomBar.setNavigationOnClickListener {
            behaviour.apply {
                state = if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        starred.setOnClickListener { onUserRetrieved { route(it?.toStarred()) } }
        repos.setOnClickListener { onUserRetrieved { route(it?.toRepos()) } }
        gists.setOnClickListener { onUserRetrieved { route(it?.toGists()) } }
        orgs.setOnClickListener {
            MultiPurposeBottomSheetDialog.show(
                childFragmentManager,
                MultiPurposeBottomSheetDialog.BottomSheetFragmentType.ORGANIZATIONS
            )
        }
        trending.setOnClickListener { route(TRENDING_LINK) }
    }

    private fun onUserRetrieved(action: (user: LoginModel?) -> Unit) {
        addDisposal(
            viewModel.login
                .subscribe({ action(it) }, ::print)
        )
    }

    private fun listenToDataChanges() {
        viewModel.progress.observeNotNull(this) {
            swipeRefresh.isRefreshing = it == true
        }
        viewModel.list.observeNotNull(this) {
            adapter.submitList(it.distinct())
        }
        viewModel.logoutProcess.observeNotNull(this) {
            if (it) {
                preference.token = null
                preference.otpCode = null
                LoginChooserActivity.startActivity(requireActivity())
            }
        }
        viewModel.unreadNotificationLiveData.observeNotNull(this) {
            bottomBar.menu?.findItem(R.id.notifications)?.icon = if (it) {
                getDrawable(R.drawable.ic_notification_unread)
            } else {
                getDrawable(R.drawable.ic_notifications_none)
            }
        }
    }

    private fun onClickListener(): (MainScreenModel) -> Unit {
        return { model: MainScreenModel ->
            model.onClick(this)
        }
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
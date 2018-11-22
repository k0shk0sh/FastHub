package com.fastaccess.github.ui.modules.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.MainScreenModel
import com.fastaccess.data.model.MainScreenModelRowType
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.MainScreenAdapter
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.fastaccess.github.utils.FEEDS_LINK
import com.fastaccess.github.utils.NOTIFICATION_LINK
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.otpCode
import com.fastaccess.github.utils.extensions.route
import com.fastaccess.github.utils.extensions.token
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.appbar_profile_title_layout.*
import kotlinx.android.synthetic.main.bottm_bar_menu_layout.*
import kotlinx.android.synthetic.main.main_fragment_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 12.06.18.
 */
class MainFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var preference: FastHubSharedPreference
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(MainFragmentViewModel::class.java) }
    private val behaviour by lazy { BottomSheetBehavior.from(bottomSheet) }
    private val adapter by lazy { MainScreenAdapter(onClickListener()) }

    override fun layoutRes(): Int = R.layout.main_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.load()
        }
        profile.isVisible = false
        swipeRefresh.setOnRefreshListener { viewModel.load() }
        toolbarTitle.setText(R.string.app_name)
        recyclerView.addDivider()
        recyclerView.adapter = adapter
        bottomBar.inflateMenu(R.menu.main_bottom_bar_menu)
        bottomBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.profile -> addDisposal(viewModel.login.subscribe({ route(it?.htmlUrl) }, ::print))
            }
            return@setOnMenuItemClickListener true
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
        listenToDataChanges()
        initClicks()
    }

    private fun initClicks() {
        navigationView.setNavigationItemSelectedListener {
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            when (it.itemId) {
                R.id.logout -> viewModel.logout()
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
        starred.setOnClickListener { view ->
            onUserRetrieved { route(it?.toStarred()) }
        }
        repos.setOnClickListener { view ->
            onUserRetrieved { route(it?.toRepos()) }
        }
        gists.setOnClickListener { view ->
            onUserRetrieved { route(it?.toGists()) }
        }
    }

    private fun onUserRetrieved(action: (user: LoginModel?) -> Unit) {
        addDisposal(viewModel.login
            .subscribe({ action(it) }, ::print))
    }

    private fun listenToDataChanges() {
        viewModel.progress.observeNotNull(this) {
            swipeRefresh.isRefreshing = it == true
        }
        viewModel.getList().observeNotNull(this) {
            adapter.submitList(it)
        }
        viewModel.logoutProcess.observeNotNull(this) {
            if (it) {
                preference.token = null
                preference.otpCode = null
                activity?.finish()
            }
        }
    }

    private fun onClickListener(): (MainScreenModel) -> Unit {
        return { model: MainScreenModel ->
            when (model.mainScreenModelRowType) {
                MainScreenModelRowType.FEED_TITLE -> route(FEEDS_LINK)
                MainScreenModelRowType.FEED -> route(model.feed?.actor?.url)
                MainScreenModelRowType.NOTIFICATION_TITLE -> route(NOTIFICATION_LINK)
                MainScreenModelRowType.NOTIFICATION -> Timber.e("${model.notificationModel}")
                MainScreenModelRowType.ISSUES_TITLE -> Timber.e("${model.mainScreenModelRowType}")
                MainScreenModelRowType.ISSUES -> Timber.e("${model.issuesPullsModel}")
                MainScreenModelRowType.PRS_TITLE -> Timber.e("${model.mainScreenModelRowType}")
                MainScreenModelRowType.PRS -> Timber.e("${model.issuesPullsModel}")
            }
        }
    }


    companion object {
        const val TAG = "MainFragment"
        fun newInstance(): MainFragment = MainFragment()
    }
}
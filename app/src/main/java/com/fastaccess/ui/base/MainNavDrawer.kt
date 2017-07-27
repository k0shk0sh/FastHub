package com.fastaccess.ui.base

import android.content.Intent
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.data.dao.model.Login
import com.fastaccess.data.dao.model.PinnedRepos
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.scheme.SchemeParser
import com.fastaccess.ui.adapter.LoginAdapter
import com.fastaccess.ui.adapter.PinnedReposAdapter
import com.fastaccess.ui.modules.about.FastHubAboutActivity
import com.fastaccess.ui.modules.gists.GistsListActivity
import com.fastaccess.ui.modules.login.chooser.LoginChooserActivity
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.main.premium.PremiumActivity
import com.fastaccess.ui.modules.notification.NotificationActivity
import com.fastaccess.ui.modules.pinned.PinnedReposActivity
import com.fastaccess.ui.modules.trending.TrendingActivity
import com.fastaccess.ui.modules.user.UserPagerActivity
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView

/**
 * Created by Kosh on 09 Jul 2017, 3:50 PM
 */
class MainNavDrawer(val view: BaseActivity<*, *>, val extraNav: NavigationView?, val accountsNav: NavigationView?)
    : BaseViewHolder.OnItemClickListener<Login> {

    var menusHolder: ViewGroup? = null
    val togglePinned: View? = view.findViewById<View>(R.id.togglePinned)
    val pinnedList: DynamicRecyclerView? = view.findViewById<DynamicRecyclerView>(R.id.pinnedList)
    val pinnedListAdapter = PinnedReposAdapter(true)
    val userModel: Login? = Login.getUser()

    init {
        menusHolder = view.findViewById<ViewGroup>(R.id.menusHolder)
        pinnedListAdapter.listener = object : BaseViewHolder.OnItemClickListener<PinnedRepos?> {
            override fun onItemClick(position: Int, v: View?, item: PinnedRepos?) {
                if (v != null && item != null) {
                    view.closeDrawer()
                    Handler().postDelayed({ SchemeParser.launchUri(v.context, item.pinnedRepo.htmlUrl) }, 250)
                }
            }

            override fun onItemLongClick(position: Int, v: View?, item: PinnedRepos?) {}
        }
        pinnedList?.adapter = pinnedListAdapter
        togglePinned?.setOnClickListener {
            view.closeDrawer()
            Handler().postDelayed({ PinnedReposActivity.startActivity(view) }, 250)
        }
    }

    fun setupViewDrawer() {
        extraNav?.let {
            val header = it.getHeaderView(0)
            setupView(header)
        }
        accountsNav?.let {
            setupAccounts()
            setupPinned()
            setupItems()
        }
    }

    private fun setupItems() {
        userModel?.let {
            view.findViewById<View>(R.id.repos).setOnClickListener {
                view.closeDrawer()
                Handler().postDelayed({ UserPagerActivity.startActivity(view, userModel.login, false, PrefGetter.isEnterprise(), 2) }, 250)
            }
            view.findViewById<View>(R.id.starred).setOnClickListener {
                view.closeDrawer()
                Handler().postDelayed({ UserPagerActivity.startActivity(view, userModel.login, false, PrefGetter.isEnterprise(), 3) }, 250)
            }
        }
    }

    private fun setupAccounts() {
        val addAccount = view.findViewById<View>(R.id.addAccLayout)
        val recyclerView = view.findViewById<DynamicRecyclerView>(R.id.accLists)
        val toggleImage = view.findViewById<View>(R.id.toggleImage)
        val toggle = view.findViewById<View>(R.id.toggle)
        val toggleAccountsLayout = view.findViewById<View>(R.id.toggleAccountsLayout)
        toggleImage.rotation = if (toggleAccountsLayout.visibility == View.VISIBLE) 180f else 0f
        addAccount.setOnClickListener {
            view.closeDrawer()
            Handler().postDelayed({
                if (PrefGetter.isProEnabled() || PrefGetter.isEnterpriseEnabled()) {
                    val intent = Intent(view, LoginChooserActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    view.startActivity(intent)
                } else {
                    view.startActivity(Intent(view, PremiumActivity::class.java))
                }
            }, 250)
        }
        toggle.setOnClickListener {
            TransitionManager.beginDelayedTransition(menusHolder ?: extraNav!!)
            val isVisible = recyclerView.visibility == View.VISIBLE
            recyclerView.visibility = if (isVisible) View.GONE else View.VISIBLE
            toggleImage.rotation = if (recyclerView.visibility == View.VISIBLE) 180f else 0f
        }
        val adapter = LoginAdapter(true)
        view.getPresenter().manageViewDisposable(Login.getAccounts()
                .doOnComplete {
                    when (!adapter.isEmpty) {
                        true -> {
                            toggleAccountsLayout.visibility = View.VISIBLE
                            adapter.listener = this
                            recyclerView.adapter = adapter
                        }
                        else -> toggleAccountsLayout.visibility = View.GONE
                    }
                }
                .subscribe({ adapter.addItem(it) }, ::print))
    }

    private fun setupPinned() {
        if (extraNav != null) {
            view.getPresenter().manageViewDisposable(PinnedRepos.getMenuRepos()
                    .subscribe({ pinnedListAdapter.insertItems(it) }, ::println))
        }
    }

    private fun setupView(view: View) {
        userModel?.let {
            (view.findViewById<View>(R.id.navAvatarLayout) as AvatarLayout).setUrl(it.avatarUrl, null, false,
                    PrefGetter.isEnterprise())
            (view.findViewById<View>(R.id.navUsername) as TextView).text = it.login
            val navFullName = view.findViewById<FontTextView>(R.id.navFullName)
            when (it.name.isNullOrBlank()) {
                true -> navFullName.visibility = View.GONE
                else -> {
                    navFullName.visibility = View.VISIBLE
                    navFullName.text = it.name
                }
            }
            view.findViewById<View>(R.id.donatedIcon).visibility = if (PrefGetter.hasSupported()) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.navAccHolder).setOnClickListener {
                if (extraNav != null && accountsNav != null) {
                    TransitionManager.beginDelayedTransition(menusHolder ?: extraNav)
                    accountsNav.visibility = if (accountsNav.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    view.findViewById<View>(R.id.navToggle).rotation = if (accountsNav.visibility == View.VISIBLE) 180f else 0f
                    setupPinned()
                }
            }
        }
    }

    fun onMainNavItemClick(item: MenuItem) {
        if (item.isChecked) return
        Handler().postDelayed({
            if (!view.isFinishing()) {
                when {
                    item.itemId == R.id.navToRepo -> view.onNavToRepoClicked()
                    item.itemId == R.id.gists -> GistsListActivity.startActivity(view, false)
                    item.itemId == R.id.pinnedMenu -> PinnedReposActivity.startActivity(view)
                    item.itemId == R.id.mainView -> {
                        val intent = Intent(view, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        view.startActivity(intent)
                        view.finish()
                    }
                    item.itemId == R.id.profile -> userModel?.let {
                        UserPagerActivity.startActivity(view, it.login, false, PrefGetter.isEnterprise(), 0)
                    }
                    item.itemId == R.id.settings -> view.onOpenSettings()
                    item.itemId == R.id.about -> view.startActivity(Intent(view, FastHubAboutActivity::class.java))
                    item.itemId == R.id.orgs -> view.onOpenOrgsDialog()
                    item.itemId == R.id.notifications -> view.startActivity(Intent(view, NotificationActivity::class.java))
                    item.itemId == R.id.trending -> view.startActivity(Intent(view, TrendingActivity::class.java))
                }
            }
        }, 250)
    }

    override fun onItemLongClick(position: Int, v: View?, item: Login) {}

    override fun onItemClick(position: Int, v: View, item: Login) {
        view.getPresenter().manageViewDisposable(RxHelper.getObservable(Login.onMultipleLogin(item, item.isIsEnterprise, false))
                .doOnSubscribe { view.showProgress(0) }
                .doOnComplete { view.hideProgress() }
                .subscribe({ view.onRestartApp() }, ::println))
    }
}
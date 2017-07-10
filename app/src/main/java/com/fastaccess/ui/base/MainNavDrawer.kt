package com.fastaccess.ui.base

import android.content.Intent
import android.os.Handler
import android.support.design.widget.NavigationView
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.adapter.LoginAdapter
import com.fastaccess.ui.modules.about.FastHubAboutActivity
import com.fastaccess.ui.modules.gists.GistsListActivity
import com.fastaccess.ui.modules.login.LoginChooserActivity
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.main.donation.DonationActivity
import com.fastaccess.ui.modules.notification.NotificationActivity
import com.fastaccess.ui.modules.pinned.PinnedReposActivity
import com.fastaccess.ui.modules.trending.TrendingActivity
import com.fastaccess.ui.modules.user.UserPagerActivity
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView

/**
 * Created by Kosh on 09 Jul 2017, 3:50 PM
 */
class MainNavDrawer(val view: BaseActivity<*, *>, val extraNav: NavigationView?, val accountsNav: NavigationView?)
    : BaseViewHolder.OnItemClickListener<Login> {

    fun setupViewDrawer() {
        extraNav?.let {
            val header = it.getHeaderView(0)
            setupView(header)
        }
        accountsNav?.let {
            setupAccounts()
        }
    }

    private fun setupAccounts() {
        val addAccount = view.findViewById<View>(R.id.addAccLayout)
        val recyclerView = view.findViewById<DynamicRecyclerView>(R.id.accLists)
        addAccount.setOnClickListener {
            val intent = Intent(view, LoginChooserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            view.startActivity(intent)
            view.finish()
        }
        val adapter = LoginAdapter()
        adapter.listener = this
        recyclerView.adapter = adapter
        view.getPresenter().manageViewDisposable(Login.getAccounts()
                .subscribe({ adapter.addItem(it) }, ::print))
    }

    private fun setupView(view: View) {
        val userModel = Login.getUser() ?: return
        (view.findViewById<View>(R.id.navAvatarLayout) as AvatarLayout).setUrl(userModel.avatarUrl, userModel.login, false, PrefGetter.isEnterprise())
        (view.findViewById<View>(R.id.navUsername) as TextView).text = userModel.login
        when (userModel.name.isNullOrEmpty()) {
            true -> view.findViewById<View>(R.id.navFullName).visibility = View.GONE
            else -> (view.findViewById<View>(R.id.navFullName) as TextView).text = userModel.name
        }
        view.findViewById<View>(R.id.donatedIcon).visibility = if (PrefGetter.hasSupported()) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.navAccHolder).setOnClickListener {
            if (extraNav != null && accountsNav != null) {
                accountsNav.visibility = if (accountsNav.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                view.findViewById<View>(R.id.navToggle).rotation = if (accountsNav.visibility == View.VISIBLE) 180f else 0f
            }
        }
    }

    fun onMainNavItemClick(item: MenuItem) {
        if (item.isChecked) return
        Handler().postDelayed({
            if (!view.isFinishing()) {
                when {
                    item.itemId == R.id.navToRepo -> view.onNavToRepoClicked()
                    item.itemId == R.id.supportDev -> view.startActivity(Intent(view, DonationActivity::class.java))
                    item.itemId == R.id.gists -> GistsListActivity.startActivity(view, false)
                    item.itemId == R.id.pinnedMenu -> PinnedReposActivity.startActivity(view)
                    item.itemId == R.id.mainView -> {
                        val intent = Intent(view, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        view.startActivity(intent)
                        view.finish()
                    }
                    item.itemId == R.id.profile -> view.startActivity(UserPagerActivity.createIntent(view, Login.getUser().login, false
                            , PrefGetter.isEnterprise()))
                    item.itemId == R.id.logout -> view.onLogoutPressed()
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
        ActivityHelper.activateLinkInterceptorActivity(v.context, !item.isIsEnterprise)
        view.getPresenter().manageViewDisposable(RxHelper.getObserver(Login.onMultipleLogin(item, item.isIsEnterprise, false))
                .doOnSubscribe { view.showProgress(0) }
                .doFinally { view.hideProgress() }
                .subscribe({ view.onRestartApp() }, ::println))
    }
}
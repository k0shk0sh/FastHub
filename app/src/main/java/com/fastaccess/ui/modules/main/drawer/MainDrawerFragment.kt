package com.fastaccess.ui.modules.main.drawer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.about.FastHubAboutActivity
import com.fastaccess.ui.modules.gists.GistsListActivity
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.modules.main.MainMvp
import com.fastaccess.ui.modules.main.donation.CheckPurchaseActivity
import com.fastaccess.ui.modules.main.playstore.PlayStoreWarningActivity
import com.fastaccess.ui.modules.notification.NotificationActivity
import com.fastaccess.ui.modules.pinned.PinnedReposActivity
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity
import com.fastaccess.ui.modules.trending.TrendingActivity
import com.fastaccess.ui.modules.user.UserPagerActivity
import kotlinx.android.synthetic.main.main_nav_fragment_layout.*

/**
 * Created by Kosh on 25.03.18.
 */
class MainDrawerFragment : BaseFragment<MainMvp.View, BasePresenter<MainMvp.View>>(), NavigationView.OnNavigationItemSelectedListener {

    private val userModel by lazy { Login.getUser() }

    override fun fragmentLayout() = R.layout.main_nav_fragment_layout

    override fun providePresenter() = BasePresenter<MainMvp.View>()

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        mainNav.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val activity = activity as? BaseActivity<*, *>? ?: return false
        activity.closeDrawer()
        if (item.isChecked) return false
        mainNav.postDelayed({
            if (!activity.isFinishing()) {
                when {
                    item.itemId == R.id.navToRepo -> activity.onNavToRepoClicked()
                    item.itemId == R.id.gists -> GistsListActivity.startActivity(activity)
                    item.itemId == R.id.pinnedMenu -> PinnedReposActivity.startActivity(activity)
                    item.itemId == R.id.mainView -> {
                        if (activity !is MainActivity) {
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            activity.startActivity(intent)
                            activity.finish()
                        }
                    }
                    item.itemId == R.id.profile -> userModel?.let {
                        UserPagerActivity.startActivity(activity, it.login, false, PrefGetter.isEnterprise(), 0)
                    }
                    item.itemId == R.id.settings -> activity.onOpenSettings()
                    item.itemId == R.id.about -> activity.startActivity(Intent(activity, FastHubAboutActivity::class.java))
                    item.itemId == R.id.orgs -> activity.onOpenOrgsDialog()
                    item.itemId == R.id.notifications -> activity.startActivity(Intent(activity, NotificationActivity::class.java))
                    item.itemId == R.id.trending -> activity.startActivity(Intent(activity, TrendingActivity::class.java))
                    item.itemId == R.id.reportBug -> activity.startActivity(CreateIssueActivity.startForResult(activity))
                    item.itemId == R.id.faq -> activity.startActivity(Intent(activity, PlayStoreWarningActivity::class.java))
                    item.itemId == R.id.restorePurchase -> activity.startActivity(Intent(activity, CheckPurchaseActivity::class.java))
                }
            }
        }, 250)
        return true
    }

    fun getMenu() = mainNav?.menu
}
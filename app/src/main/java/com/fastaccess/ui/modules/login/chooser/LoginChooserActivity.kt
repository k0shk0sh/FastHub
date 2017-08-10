package com.fastaccess.ui.modules.login.chooser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.transition.TransitionManager
import android.view.View
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.OnClick
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.LoginAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.login.LoginActivity
import com.fastaccess.ui.modules.main.premium.PremiumActivity
import com.fastaccess.ui.modules.settings.LanguageBottomSheetDialog
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import io.reactivex.functions.Action
import java.util.*

/**
 * Created by Kosh on 28 Apr 2017, 9:03 PM
 */

class LoginChooserActivity : BaseActivity<LoginChooserMvp.View, LoginChooserPresenter>(), LoginChooserMvp.View {

    @BindView(R.id.language_selector) lateinit var language_selector: RelativeLayout
    @BindView(R.id.recycler) lateinit var recycler: DynamicRecyclerView
    @BindView(R.id.multiAccLayout) lateinit var multiAccLayout: View
    @BindView(R.id.viewGroup) lateinit var viewGroup: CoordinatorLayout
    @BindView(R.id.toggleImage) lateinit var toggleImage: View

    private val adapter = LoginAdapter()

    override fun layout(): Int = R.layout.login_chooser_layout

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = false

    override fun isSecured(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.listener = this
        recycler.adapter = adapter
        val languages = resources.getStringArray(R.array.languages_array_values)
        if (Locale.getDefault().language in languages) {
            val language = PrefGetter.getAppLanguage()
            PrefGetter.setAppLangauge(Locale.getDefault().language)
            if (!BuildConfig.DEBUG) language_selector.visibility = View.GONE
            if (Locale.getDefault().language != language) recreate()
        }
    }

    @OnClick(R.id.basicAuth) fun onBasicAuthClicked() {
        LoginActivity.start(this, true)
    }

    @OnClick(R.id.accessToken) fun onAccessTokenClicked() {
        LoginActivity.start(this, false)
    }

    @OnClick(R.id.enterprise) internal fun onEnterpriseClicked() {
        if (Login.hasNormalLogin()) {
            if (PrefGetter.isAllFeaturesUnlocked() || PrefGetter.isEnterpriseEnabled()) {
                LoginActivity.start(this, true, true)
            } else {
                startActivity(Intent(this, PremiumActivity::class.java))
            }
        } else {
            MessageDialogView.newInstance(getString(R.string.warning), getString(R.string.enterprise_login_warning),
                    false, Bundler.start().put("hide_buttons", true).end())
                    .show(supportFragmentManager, MessageDialogView.TAG)
        }
    }

    @OnClick(R.id.browserLogin) internal fun onOpenBrowser() {
        LoginActivity.startOAuth(this)
    }

    @OnClick(R.id.language_selector_clicker) fun onChangeLanguage() {
        showLanguage()
    }

    @OnClick(R.id.toggle) internal fun onToggle() {
        TransitionManager.beginDelayedTransition(viewGroup)
        val isVisible = recycler.visibility == View.VISIBLE
        recycler.visibility = if (isVisible) View.GONE else View.VISIBLE
        toggleImage.rotation = if (!isVisible) 180f else 0f
    }

    override fun onLanguageChanged(action: Action) {
        try {
            action.run()
            recreate()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun providePresenter(): LoginChooserPresenter {
        return LoginChooserPresenter()
    }

    override fun onAccountsLoaded(accounts: List<Login>?) {
        if (accounts == null || accounts.isEmpty()) {
            multiAccLayout.visibility = View.GONE
        } else {
            TransitionManager.beginDelayedTransition(viewGroup)
            adapter.insertItems(accounts)
            multiAccLayout.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(position: Int, v: View, item: Login) {
        presenter.manageViewDisposable(Login.onMultipleLogin(item, item.isIsEnterprise, false)
                .doOnSubscribe { showProgress(0) }
                .doOnComplete { this.hideProgress() }
                .subscribe({ onRestartApp() }, ::println))
    }

    override fun onItemLongClick(position: Int, v: View, item: Login) {}

    private fun showLanguage() {
        val languageBottomSheetDialog = LanguageBottomSheetDialog()
        languageBottomSheetDialog.onAttach(this as Context)
        languageBottomSheetDialog.show(supportFragmentManager, "LanguageBottomSheetDialog")
    }

}

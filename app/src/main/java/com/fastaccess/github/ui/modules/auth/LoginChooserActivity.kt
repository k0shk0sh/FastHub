package com.fastaccess.github.ui.modules.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.github.BuildConfig
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.modules.auth.callback.LoginChooserCallback
import com.fastaccess.github.ui.modules.auth.chooser.LoginChooserFragment
import com.fastaccess.github.ui.modules.auth.login.AuthLoginFragment
import com.fastaccess.github.ui.modules.main.MainActivity
import com.fastaccess.github.utils.REDIRECT_URL
import com.fastaccess.github.utils.extensions.*
import javax.inject.Inject

/**
 * Created by Kosh on 18.05.18.
 */
class LoginChooserActivity : BaseActivity(), LoginChooserCallback {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(LoginChooserViewModel::class.java) }

    override fun layoutRes(): Int = R.layout.login_chooser_activity_layout
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.LoginTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginChooserFragment(), LoginChooserFragment.TAG)
                .commit()
        }
    }

    override fun navToBasicAuth(view: View) {
        supportFragmentManager.beginTransaction()
            .addSharedElement(view, ViewCompat.getTransitionName(view) ?: "")
            .replace(R.id.container, AuthLoginFragment.newInstance(), AuthLoginFragment.TAG)
            .addToBackStack(AuthLoginFragment.TAG)
            .commit()
    }

    override fun navToAccessToken(view: View) {
        supportFragmentManager.beginTransaction()
            .addSharedElement(view, ViewCompat.getTransitionName(view) ?: "")
            .replace(R.id.container, AuthLoginFragment.newInstance(accessToken = true), AuthLoginFragment.TAG)
            .addToBackStack(AuthLoginFragment.TAG)
            .commit()
    }

    override fun navToEnterprise(view: View) {
        supportFragmentManager.beginTransaction()
            .addSharedElement(view, ViewCompat.getTransitionName(view) ?: "")
            .replace(R.id.container, AuthLoginFragment.newInstance(isEnterprise = true), AuthLoginFragment.TAG)
            .addToBackStack(AuthLoginFragment.TAG)
            .commit()
    }

    override fun loginWithBrowser() {
        val tabIntent = CustomTabsIntent.Builder()
            .setToolbarColor(getColorAttr(R.attr.colorPrimary))
            .enableUrlBarHiding()
            .build()

        tabIntent.launchUrl(this, Uri.Builder()
            .scheme("https")
            .authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .appendQueryParameter("redirect_uri", REDIRECT_URL)
            .appendQueryParameter("scope", "user,repo,gist,notifications,read:org")
            .appendQueryParameter("state", BuildConfig.APPLICATION_ID)
            .build())
    }

    override fun popStack() = supportFragmentManager.popBackStack()

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {}

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
        setIntent(null)
    }

    override fun onResume() {
        super.onResume()
        handleIntent(intent)
        intent = null
    }

    override fun onUserLoggedIn(login: LoginModel) {
        if (login.isEnterprise == true) {
            preference.enterpriseOtpCode = login.otpCode
            preference.enterpriseToken = login.token
            preference.enterpriseUrl = login.enterpriseUrl
        } else {
            preference.token = login.token
            preference.otpCode = login.otpCode
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun handleIntent(intent: Intent?) {
        if (!viewModel.loggedInUser.hasActiveObservers()) {
            viewModel.loggedInUser.observeNotNull(this) { onUserLoggedIn(it) }
        }
        intent?.data?.let { viewModel.handleBrowserLogin(it) }
    }

    companion object {
        fun startActivity(activity: Activity) {
            val intent = Intent(activity, LoginChooserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            activity.finishAffinity()
        }
    }
}
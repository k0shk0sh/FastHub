package com.fastaccess.github.ui.modules.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import com.fastaccess.github.di.annotations.ForActivity
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.ui.modules.auth.callback.LoginChooserCallback
import com.fastaccess.github.ui.modules.auth.chooser.LoginChooserFragment
import com.fastaccess.github.ui.modules.auth.login.BaseAuthLoginFragment

/**
 * Created by Kosh on 18.05.18.
 */
class LoginChooserActivity : BaseActivity(), LoginChooserCallback {

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
                .replace(R.id.container, BaseAuthLoginFragment.newInstance(), BaseAuthLoginFragment.TAG)
                .addToBackStack(BaseAuthLoginFragment.TAG)
                .commit()
    }

    override fun navToAccessToken(view: View) {
        supportFragmentManager.beginTransaction()
                .addSharedElement(view, ViewCompat.getTransitionName(view) ?: "")
                .replace(R.id.container, BaseAuthLoginFragment.newInstance(accessToken = true), BaseAuthLoginFragment.TAG)
                .addToBackStack(BaseAuthLoginFragment.TAG)
                .commit()
    }

    override fun navToEnterprise(view: View) {
        supportFragmentManager.beginTransaction()
                .addSharedElement(view, ViewCompat.getTransitionName(view) ?: "")
                .replace(R.id.container, BaseAuthLoginFragment.newInstance(isEnterprise = true), BaseAuthLoginFragment.TAG)
                .addToBackStack(BaseAuthLoginFragment.TAG)
                .commit()
    }

    override fun popStack() = supportFragmentManager.popBackStack()

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {}

    companion object {
        fun startActivity(@ForActivity activity: Activity) {
            val intent = Intent(activity, LoginChooserActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            activity.finishAffinity()
        }
    }
}
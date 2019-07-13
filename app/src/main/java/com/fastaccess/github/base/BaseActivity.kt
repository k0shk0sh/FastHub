package com.fastaccess.github.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.di.modules.AuthenticationInterceptor
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.github.utils.IS_ENTERPRISE
import com.fastaccess.github.utils.extensions.materialize
import com.fastaccess.github.utils.extensions.otpCode
import com.fastaccess.github.utils.extensions.theme
import com.fastaccess.github.utils.extensions.token
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseActivity : DaggerAppCompatActivity(), ActivityCallback {

    @Inject lateinit var preference: FastHubSharedPreference
    @Inject lateinit var interceptor: AuthenticationInterceptor

    @LayoutRes abstract fun layoutRes(): Int

    abstract fun onActivityCreatedWithUser(savedInstanceState: Bundle?)
    override fun isLoggedIn() = !preference.token.isNullOrEmpty()
    override fun isEnterprise(): Boolean = intent?.extras?.getBoolean(IS_ENTERPRISE) ?: false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) // for the sake of theme engine
        ThemeEngine.setTheme(this, preference.theme)
        super.onCreate(savedInstanceState)
        val layoutRes = layoutRes()
        if (layoutRes > 0) setContentView(layoutRes)

        if (!isLoggedIn() && this is LoginChooserActivity) {
            onActivityCreated(savedInstanceState)
            return
        }

        if (isLoggedIn()) {
            interceptor.token = preference.token
            interceptor.otp = preference.otpCode
            onActivityCreatedWithUser(savedInstanceState)
        } else {
            LoginChooserActivity.startActivity(this)
            onActivityCreated(savedInstanceState)
        }

    }

    override fun showSnackBar(root: View, resId: Int?, message: String?, duration: Int) {
        if ((resId == null && message == null) || isFinishing) return
        Snackbar.make(root, message ?: getString(resId ?: R.string.unknown), duration)
                .materialize()
                .show()
    }

    protected open fun onActivityCreated(savedInstanceState: Bundle?) = Unit
}

interface ActivityCallback {
    fun isLoggedIn(): Boolean
    fun isEnterprise(): Boolean
    fun showSnackBar(root: View, resId: Int? = null, message: String? = null, duration: Int = Snackbar.LENGTH_LONG)
}
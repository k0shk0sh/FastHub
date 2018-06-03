package com.fastaccess.github.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import com.fastaccess.github.utils.BundleConstant
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseActivity : DaggerAppCompatActivity(), ActivityCallback {

    @Inject lateinit var preference: FastHubSharedPreference
    @Inject lateinit var themeEngine: ThemeEngine

    @LayoutRes abstract fun layoutRes(): Int

    abstract fun onActivityCreated(savedInstanceState: Bundle?)
    abstract fun onActivityCreatedWithUser(savedInstanceState: Bundle?)
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        val theme = themeEngine.getTheme(this)
        if (theme > 0) {
            setTheme(theme)
        }
        super.onCreate(savedInstanceState)
        val layoutRes = layoutRes()
        if (layoutRes > 0) setContentView(layoutRes)

        if (!isLoggedIn() && this is LoginChooserActivity) {
            onActivityCreated(savedInstanceState)
            return
        }

        if (isLoggedIn()) {
            onActivityCreatedWithUser(savedInstanceState)
        } else {
            LoginChooserActivity.startActivity(this)
            onActivityCreated(savedInstanceState)
        }

    }

    override fun isLoggedIn() = !((preference.get("loggedIn", null) as String?).isNullOrBlank())
    override fun isEnterprise(): Boolean = intent?.extras?.getBoolean(BundleConstant.IS_ENTERPRISE) ?: false
}

interface ActivityCallback {
    fun isLoggedIn(): Boolean
    fun isEnterprise(): Boolean
}
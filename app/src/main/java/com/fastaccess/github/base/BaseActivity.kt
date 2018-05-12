package com.fastaccess.github.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import com.fastaccess.data.storage.FastHubSharedPreference
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject lateinit var preference: FastHubSharedPreference

    @LayoutRes abstract fun layoutRes(): Int
    abstract fun onActivityCreated(savedInstanceState: Bundle?)
    abstract fun onActivityCreatedWithUser(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutRes = layoutRes()
        if (layoutRes > 0) setContentView(layoutRes)
        if (isLoggedIn()) {
            onActivityCreatedWithUser(savedInstanceState)
        } else {
            onActivityCreated(savedInstanceState)
        }

    }

    protected fun isLoggedIn() = !((preference.get("loggedIn", null) as String?).isNullOrBlank())
}
package com.fastaccess.github.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import dagger.android.support.DaggerFragment

/**
 * Created by Kosh on 13.05.18.
 */
abstract class BaseFragment : DaggerFragment(), ActivityCallback {

    private var activityCallback: ActivityCallback? = null

    @LayoutRes abstract fun layoutRes(): Int
    abstract fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?)
    abstract fun onFragmentCreated(view: View, savedInstanceState: Bundle?)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activityCallback = context as? ActivityCallback
    }

    override fun onDetach() {
        activityCallback = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) { // expose so its easier to find
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isLoggedIn()) {
            onFragmentCreatedWithUser(view, savedInstanceState)
        } else {
            onFragmentCreated(view, savedInstanceState)
        }
    }

    override fun isLoggedIn(): Boolean = activityCallback?.isLoggedIn() ?: false

    override fun isEnterprise(): Boolean = activityCallback?.isEnterprise() ?: false

    override fun showSnackBar(root: View, resId: Int?, message: String?, duration: Int) {
        activityCallback?.showSnackBar(root, resId, message, duration)
    }
}
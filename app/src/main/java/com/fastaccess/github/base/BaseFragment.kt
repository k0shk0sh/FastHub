package com.fastaccess.github.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment

/**
 * Created by Kosh on 13.05.18.
 */
abstract class BaseFragment : DaggerFragment() {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activityCallback?.isLoggedIn() == true) {
            onFragmentCreatedWithUser(view, savedInstanceState)
        } else {
            onFragmentCreated(view, savedInstanceState)
        }
    }
}
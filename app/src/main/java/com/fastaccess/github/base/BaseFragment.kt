package com.fastaccess.github.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.github.R
import com.fastaccess.github.utils.extensions.observeNotNull
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 13.05.18.
 */
abstract class BaseFragment : DaggerFragment(), ActivityCallback {

    private var activityCallback: ActivityCallback? = null
    private var disposal = CompositeDisposable()

    @LayoutRes abstract fun layoutRes(): Int
    abstract fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?)
    protected open fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {}
    abstract fun viewModel(): BaseViewModel?

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

        viewModel()?.progress?.observe(this, Observer {
            val refresh = this@BaseFragment.view?.findViewById<SwipeRefreshLayout?>(R.id.swipeRefresh)
            refresh?.isRefreshing = it == true
        })

        viewModel()?.error?.observeNotNull(this) {
            this@BaseFragment.view?.let { view -> showSnackBar(view, resId = it.resId, message = it.message) }
        }
    }

    override fun onDestroyView() {
        disposal.clear()
        super.onDestroyView()
    }

    override fun isLoggedIn(): Boolean = activityCallback?.isLoggedIn() ?: false

    override fun isEnterprise(): Boolean = activityCallback?.isEnterprise() ?: false

    override fun showSnackBar(root: View, resId: Int?, message: String?, duration: Int) {
        activityCallback?.showSnackBar(root, resId, message, duration)
    }

    open fun onScrollToTop() {
        view?.findViewById<RecyclerView?>(R.id.recyclerView)?.scrollToPosition(0)
    }

    fun addDisposal(disposable: Disposable) {
        disposal.add(disposable)
    }

    fun removeAndAddDisposal(disposable: Disposable) {
        disposal.remove(disposable)
        disposal.add(disposable)
    }
}
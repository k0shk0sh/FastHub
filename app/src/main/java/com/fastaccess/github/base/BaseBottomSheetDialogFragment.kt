package com.fastaccess.github.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.github.R
import com.fastaccess.github.extensions.observeNotNull
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-25.
 */
abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(), HasSupportFragmentInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    private var disposal = CompositeDisposable()
    private var activityCallback: ActivityCallback? = null

    @LayoutRes abstract fun layoutRes(): Int
    abstract fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?)
    protected open fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {}
    abstract fun viewModel(): BaseViewModel?

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityCallback = when {
            parentFragment is ActivityCallback -> parentFragment as ActivityCallback
            context is ActivityCallback -> context
            else -> null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { // expose so its easier to find
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val contextThemeWrapper = ContextThemeWrapper(context, context?.theme)
        val themeAwareInflater = inflater.cloneInContext(contextThemeWrapper)
        return themeAwareInflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activityCallback?.isLoggedIn() == true) {
            onFragmentCreatedWithUser(view, savedInstanceState)
        } else {
            onFragmentCreated(view, savedInstanceState)
        }

        viewModel()?.progress?.observe(this, Observer {
            val refresh = this@BaseBottomSheetDialogFragment.view?.findViewById<SwipeRefreshLayout?>(R.id.swipeRefresh)
            refresh?.isRefreshing = it == true
        })

        viewModel()?.error?.observeNotNull(this) {
            this@BaseBottomSheetDialogFragment.view?.let { view -> activityCallback?.showSnackBar(view, resId = it.resId, message = it.message) }
        }
    }

    override fun onDestroyView() {
        disposal.clear()
        super.onDestroyView()
    }

    protected fun addDisposal(disposable: Disposable) {
        disposal.add(disposable)
    }

    protected fun removeAndAddDisposal(disposable: Disposable) {
        disposal.remove(disposable)
        disposal.add(disposable)
    }

    protected fun setupToolbar(resId: Int, menuId: Int? = null) {
        view?.findViewById<Toolbar?>(R.id.toolbar)?.apply {
            val titleText = findViewById<TextView?>(R.id.toolbarTitle)
            if (titleText != null) {
                titleText.setText(resId)
            } else {
                setTitle(resId)
            }
            setNavigationOnClickListener { dismiss() }
            menuId?.let { inflateMenu(it) }
        }
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment>? = this.childFragmentInjector
}
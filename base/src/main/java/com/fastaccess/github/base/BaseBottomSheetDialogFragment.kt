package com.fastaccess.github.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.fastaccess.github.base.extensions.setBottomSheetCallback
import com.fastaccess.github.base.widget.ParentSwipeRefreshLayout
import com.fastaccess.github.base.widget.recyclerview.BaseRecyclerView
import com.fastaccess.github.base.widget.recyclerview.RecyclerViewFastScroller
import com.fastaccess.github.extensions.observeNotNull
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-25.
 */
abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(), HasAndroidInjector {

    @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

    private var disposal = CompositeDisposable()
    private var activityCallback: ActivityCallback? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    @LayoutRes abstract fun layoutRes(): Int
    abstract fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?)
    abstract fun viewModel(): BaseViewModel?
    protected open fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {}
    protected open fun isFullScreen(): Boolean = false

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityCallback = when {
            parentFragment is ActivityCallback -> parentFragment as ActivityCallback
            context is ActivityCallback -> context
            else -> null
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val contextThemeWrapper = ContextThemeWrapper(context, context?.theme)
        val themeAwareInflater = inflater.cloneInContext(contextThemeWrapper)
        val view = themeAwareInflater.inflate(layoutRes(), container, false)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                onGlobalLayoutChanged(view)
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activityCallback?.isLoggedIn() == true) {
            onFragmentCreatedWithUser(view, savedInstanceState)
        } else {
            onFragmentCreated(view, savedInstanceState)
        }

        viewModel()?.progress?.observeNotNull(this) {
            val refresh = this@BaseBottomSheetDialogFragment.view?.findViewById<SwipeRefreshLayout?>(R.id.swipeRefresh)
            refresh?.isRefreshing = it
        }

        viewModel()?.error?.observeNotNull(this) {
            this@BaseBottomSheetDialogFragment.view?.let { view -> activityCallback?.showSnackBar(view, resId = it.resId, message = it.message) }
        }
    }

    override fun onDestroyView() {
        disposal.clear()
        super.onDestroyView()
    }

    fun dismissDialog() = dialog?.dismiss()

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

    private fun onGlobalLayoutChanged(view: View) {
        val parent = dialog?.findViewById<ViewGroup>(R.id.design_bottom_sheet)
        if (parent != null) {
            val toggleArrow = view.findViewById<ImageView?>(R.id.toggleArrow)
            parent.setBackgroundColor(Color.TRANSPARENT)
            bottomSheetBehavior = BottomSheetBehavior.from(parent)
            if (isFullScreen()) {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
            bottomSheetBehavior?.setBottomSheetCallback({ newState ->
                if (newState == BottomSheetBehavior.STATE_HIDDEN) dialog?.cancel()
            })
            bottomSheetBehavior?.let { behaviour ->
                toggleArrow?.setOnClickListener {
                    toggleArrow.setOnClickListener {
                        if (behaviour.state != BottomSheetBehavior.STATE_EXPANDED) {
                            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        } else {
                            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            }
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = this.childFragmentInjector

    protected val appBar by lazy { view?.findViewById<AppBarLayout>(R.id.appBar) ?: throw IllegalAccessError("error") }
    protected val toolbar by lazy { view?.findViewById<Toolbar>(R.id.toolbar) ?: throw IllegalAccessError("error") }
    protected val emptyLayout by lazy { view?.findViewById<View>(R.id.emptyLayout) ?: throw IllegalAccessError("error") }
    protected val toolbarTitle by lazy { view?.findViewById<TextView>(R.id.toolbarTitle) ?: throw IllegalAccessError("error") }
    protected val tabs by lazy { view?.findViewById<TabLayout>(R.id.tabs) ?: throw IllegalAccessError("error") }
    protected val pager by lazy { view?.findViewById<ViewPager>(R.id.pager) ?: throw IllegalAccessError("error") }
    protected val recyclerView by lazy { view?.findViewById<BaseRecyclerView>(R.id.recyclerView) ?: throw IllegalAccessError("error") }
    protected val fastScroller by lazy { view?.findViewById<RecyclerViewFastScroller>(R.id.fastScroller) ?: throw IllegalAccessError("error") }
    protected val swipeRefresh by lazy { view?.findViewById<ParentSwipeRefreshLayout>(R.id.swipeRefresh) ?: throw IllegalAccessError("error") }
}
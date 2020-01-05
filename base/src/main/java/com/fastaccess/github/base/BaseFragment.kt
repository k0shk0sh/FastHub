package com.fastaccess.github.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager
import com.fastaccess.data.model.FragmentType
import com.fastaccess.github.base.adapter.PagerAdapter
import com.fastaccess.github.base.callback.UpdateTabCount
import com.fastaccess.github.base.widget.ParentSwipeRefreshLayout
import com.fastaccess.github.base.widget.recyclerview.BaseRecyclerView
import com.fastaccess.github.base.widget.recyclerview.RecyclerViewFastScroller
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 13.05.18.
 */
abstract class BaseFragment : DaggerFragment(), ActivityCallback, UpdateTabCount {

    protected var updateCountCallback: UpdateTabCount? = null
    private var activityCallback: ActivityCallback? = null
    private var disposal = CompositeDisposable()

    abstract fun viewModel(): BaseViewModel?
    @LayoutRes abstract fun layoutRes(): Int

    abstract fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    )

    protected open fun onFragmentCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallback = context as? ActivityCallback
        updateCountCallback = when {
            parentFragment is UpdateTabCount -> parentFragment as UpdateTabCount
            context is UpdateTabCount -> context
            else -> null
        }
    }

    override fun onDetach() {
        activityCallback = null
        updateCountCallback = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) { // expose so its easier to find
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        if (isLoggedIn()) {
            onFragmentCreatedWithUser(view, savedInstanceState)
        } else {
            onFragmentCreated(view, savedInstanceState)
        }

        viewModel()?.progress?.observeNotNull(this) {
            val refresh = this@BaseFragment.view?.findViewById<SwipeRefreshLayout?>(R.id.swipeRefresh)
            refresh?.isRefreshing = it
        }

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

    override fun showSnackBar(
        root: View,
        resId: Int?,
        message: String?,
        duration: Int
    ) {
        activityCallback?.showSnackBar(root, resId, message, duration)
    }

    override fun updateCount(
        type: FragmentType,
        count: Int
    ) {
        val pager = view?.findViewById<ViewPager?>(R.id.pager) ?: return
        val tabs = view?.findViewById<TabLayout?>(R.id.tabs) ?: return
        val adapter = pager.adapter as? PagerAdapter ?: return
        val index = adapter.getIndex(type)
        if (index == -1) return
        val model = adapter.getModel(index)
        tabs.getTabAt(index)?.let {
            it.text = SpannableBuilder.builder()
                .append(model?.text ?: "", LabelSpan(Color.TRANSPARENT))
                .space()
                .append(" $count ", LabelSpan(requireContext().getColorAttr(R.attr.colorAccent)))
        }
    }

    protected fun postCount(
        type: FragmentType,
        count: Int
    ) {
        updateCountCallback?.updateCount(type, count)
    }

    protected fun startTransactionDelay(view: View?) {
        view?.let { TransitionManager.beginDelayedTransition(it as ViewGroup) }
    }

    open fun onScrollToTop() {
        view?.findViewById<RecyclerView?>(R.id.recyclerView)?.scrollToPosition(0)
    }

    open fun onBackPressed(): Boolean = true

    fun addDisposal(disposable: Disposable) {
        disposal.add(disposable)
    }

    fun removeAndAddDisposal(disposable: Disposable) {
        disposal.remove(disposable)
        disposal.add(disposable)
    }

    fun dismiss() {
        when (parentFragment) {
            is BaseBottomSheetDialogFragment -> (parentFragment as? BaseBottomSheetDialogFragment)?.dismissDialog()
            is BaseDialogFragment -> (parentFragment as? BaseDialogFragment)?.dismissDialog()
            else -> activity?.onBackPressed()
        }
    }

    protected fun setupToolbar(
        title: String,
        menuId: Int? = null,
        onMenuItemClick: ((item: MenuItem) -> Unit)? = null
    ) {
        view?.findViewById<Toolbar?>(R.id.toolbar)?.apply {
            val titleText = findViewById<TextView?>(R.id.toolbarTitle)
            if (titleText != null) {
                titleText.text = title
            } else {
                setTitle(title)
            }
            setNavigationOnClickListener { dismiss() }
            menuId?.let { menuResId ->
                inflateMenu(menuResId)
                onMenuItemClick?.let { onClick ->
                    setOnMenuItemClickListener {
                        onClick.invoke(it)
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }


    protected fun setupToolbar(
        resId: Int,
        menuId: Int? = null,
        onMenuItemClick: ((item: MenuItem) -> Unit)? = null
    ) = setupToolbar(getString(resId), menuId, onMenuItemClick)

    protected fun setToolbarNavigationIcon(resId: Int) = view?.findViewById<Toolbar?>(R.id.toolbar)?.setNavigationIcon(resId) ?: Unit

    protected val appBar by lazy { view?.findViewById<AppBarLayout>(R.id.appBar) }
    protected val toolbar by lazy { view?.findViewById<Toolbar>(R.id.toolbar) }
    protected val toolbarTitle by lazy { view?.findViewById<TextView>(R.id.toolbarTitle) }
    protected val emptyLayout by lazy { view?.findViewById<View>(R.id.emptyLayout) ?: throw IllegalAccessError("error") }
    protected val tabs by lazy { view?.findViewById<TabLayout>(R.id.tabs) ?: throw IllegalAccessError("error") }
    protected val pager by lazy { view?.findViewById<ViewPager>(R.id.pager) ?: throw IllegalAccessError("error") }
    protected val recyclerView by lazy { view?.findViewById<BaseRecyclerView>(R.id.recyclerView) ?: throw IllegalAccessError("error") }
    protected val fastScroller by lazy { view?.findViewById<RecyclerViewFastScroller>(R.id.fastScroller) ?: throw IllegalAccessError("error") }
    protected val swipeRefresh by lazy { view?.findViewById<ParentSwipeRefreshLayout>(R.id.swipeRefresh) ?: throw IllegalAccessError("error") }
}
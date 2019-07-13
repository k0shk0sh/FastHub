package com.fastaccess.github.ui.widget.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fastaccess.github.R
import com.google.android.material.appbar.AppBarLayout

open class RecyclerViewFastScroller : FrameLayout {

    private lateinit var scrollerView: ImageView
    private var scrollerHeight: Int = 0
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var appBarLayout: AppBarLayout? = null
    private var toggled: Boolean = false

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (scrollerView.isSelected) return
            val verticalScrollOffset = recyclerView.computeVerticalScrollOffset()
            val verticalScrollRange = recyclerView.computeVerticalScrollRange()
            val proportion = verticalScrollOffset.toFloat() / (verticalScrollRange.toFloat() - scrollerHeight)
            setScrollerHeight(scrollerHeight * proportion)
        }
    }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            hideShow()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            hideShow()
        }

        override fun onChanged() {
            super.onChanged()
            hideShow()

        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scrollerHeight = h
    }

    @SuppressLint("ClickableViewAccessibility") override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < scrollerView.x - scrollerView.paddingStart) return false
                scrollerView.isSelected = true
                hideAppbar()
                val y = event.y
                setScrollerHeight(y)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.y
                setScrollerHeight(y)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                scrollerView.isSelected = false
                showAppbar()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        recyclerView?.removeOnScrollListener(onScrollListener)
        safelyUnregisterObserver()
        appBarLayout = null
        super.onDetachedFromWindow()
    }

    private fun safelyUnregisterObserver() {
        kotlin.runCatching {
            recyclerView?.adapter?.unregisterAdapterDataObserver(observer)
        }
    }

    private fun init() {
        visibility = View.GONE
        clipChildren = false
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.fastscroller_layout, this)
        scrollerView = findViewById(R.id.fast_scroller_handle)
        visibility = View.VISIBLE
    }

    private fun hideAppbar() {
        if (!toggled) {
            appBarLayout?.setExpanded(false, true)
            toggled = true
        }
    }

    private fun showAppbar() {
        if (toggled) {
            if (scrollerView.y == 0f) {
                appBarLayout?.setExpanded(true, true)
                toggled = false
            }
        }
    }

    fun attachRecyclerView(recyclerView: RecyclerView, appBarLayout: AppBarLayout? = null) {
        this.appBarLayout = appBarLayout
        if (this.recyclerView == null) {
            this.recyclerView = recyclerView
            this.layoutManager = recyclerView.layoutManager
            this.recyclerView?.addOnScrollListener(onScrollListener)
            if (recyclerView.adapter != null) {
                runCatching { recyclerView.adapter?.registerAdapterDataObserver(observer) }
            }
            hideShow()
            initScrollHeight()
        }
    }

    private fun initScrollHeight() {
        if (recyclerView?.computeVerticalScrollOffset() == 0) {
            this.recyclerView?.viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    this@RecyclerViewFastScroller.recyclerView?.viewTreeObserver?.removeOnPreDrawListener(this)
                    iniHeight()
                    return true
                }
            })
        } else {
            iniHeight()
        }
    }

    protected fun iniHeight() {
        if (scrollerView.isSelected) return
        val verticalScrollOffset = this@RecyclerViewFastScroller.recyclerView?.computeVerticalScrollOffset() ?: 0
        val verticalScrollRange = this@RecyclerViewFastScroller.computeVerticalScrollRange()
        val proportion = verticalScrollOffset.toFloat() / (verticalScrollRange.toFloat() - scrollerHeight)
        setScrollerHeight(scrollerHeight * proportion)
    }

    private fun setRecyclerViewPosition(y: Float) {
        if (recyclerView != null) {
            val itemCount = recyclerView?.adapter?.itemCount ?: 0
            val proportion: Float = when {
                scrollerView.y == 0f -> 0f
                scrollerView.y + scrollerView.height >= scrollerHeight - TRACK_SNAP_RANGE -> 1f
                else -> y / scrollerHeight.toFloat()
            }
            val targetPos = getValueInRange(itemCount - 1, (proportion * itemCount).toInt())
            when (layoutManager) {
                is StaggeredGridLayoutManager -> (layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(targetPos, 0)
                is GridLayoutManager -> (layoutManager as GridLayoutManager).scrollToPositionWithOffset(targetPos, 0)
                else -> (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(targetPos, 0)
            }
        }
    }

    private fun setScrollerHeight(y: Float) {
        val handleHeight = scrollerView.height
        scrollerView.y = getValueInRange(scrollerHeight - handleHeight, (y - handleHeight / 2).toInt()).toFloat()
    }

    protected fun hideShow() {
        visibility = if (recyclerView != null && recyclerView?.adapter != null) {
            if (recyclerView?.adapter?.itemCount ?: 0 > 10) View.VISIBLE else View.GONE
        } else {
            View.GONE
        }
    }

    companion object {
        private const val TRACK_SNAP_RANGE = 5
        private fun getValueInRange(max: Int, value: Int): Int {
            return Math.min(Math.max(0, value), max)
        }
    }
}
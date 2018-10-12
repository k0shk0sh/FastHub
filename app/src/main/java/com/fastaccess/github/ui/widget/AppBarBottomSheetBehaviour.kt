package com.fastaccess.github.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.fastaccess.github.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AppBarBottomSheetBehaviour(context: Context, attrs: AttributeSet?) : AppBarLayout.Behavior(context, attrs) {
    private var hasBottomSheet = false

    override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, nestedScrollAxes: Int,
                                     type: Int): Boolean {
        val bottomSheet = parent.findViewById<View?>(R.id.bottomSheet)
        hasBottomSheet = isOpen(bottomSheet)
        return !hasBottomSheet && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_CANCEL) {
            hasBottomSheet = false
        }
        return !hasBottomSheet && super.onInterceptTouchEvent(parent, child, ev)
    }

    private fun isOpen(bottomSheet: View?): Boolean = getBottomSheetState(bottomSheet) != BottomSheetBehavior.STATE_COLLAPSED

    private fun getBottomSheetState(bottomSheet: View?): Int? {
        val view = bottomSheet ?: return null
        return try {
            BottomSheetBehavior.from(view)?.state
        } catch (e: Exception) {
            AnchorSheetBehavior.from(view)?.state
        }
    }
}
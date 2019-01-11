package com.fastaccess.github.ui.widget.recyclerview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeToDeleteCallback(
    private val callback: (viewHolder: RecyclerView.ViewHolder, direction: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val background = ColorDrawable()
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder !is AllowSwipeToDelete) return 0 // if the viewholder doesn't impl AllowSwipeToDelete then lets stop.
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        val delegate = viewHolder as? AllowSwipeToDelete
            ?: return super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(canvas, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (dX > 0f) {
            delegate.drawableStart?.let {
                background.color = delegate.drawableStartBackground
                background.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                background.draw(canvas)

                val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                val iconMargin = (itemHeight - it.intrinsicHeight) / 2
                val iconLeft = itemView.left + iconMargin
                val iconRight = iconLeft + it.intrinsicWidth
                val iconBottom = iconTop + it.intrinsicHeight

                // Draw the delete icon
                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(canvas)

            }
        } else {
            delegate.drawableEnd?.let { firstAction ->
                background.color = delegate.drawableEndBackground
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(canvas)

                val firstIconTop = itemView.top + (itemHeight - firstAction.intrinsicHeight) / 2
                val firstIconMargin = (itemHeight - firstAction.intrinsicHeight) / 2
                val firstIconLeft = itemView.right - firstIconMargin - firstAction.intrinsicWidth
                val firstIconRight = itemView.right - firstIconMargin
                val firstIconBottom = firstIconTop + firstAction.intrinsicHeight
                firstAction.setBounds(firstIconLeft, firstIconTop, firstIconRight, firstIconBottom)
                firstAction.draw(canvas)
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        callback.invoke(viewHolder, direction)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }


    interface AllowSwipeToDelete {
        val drawableStart: Drawable?
        val drawableStartBackground: Int
        val drawableEnd: Drawable?
        val drawableEndBackground: Int
    }
}


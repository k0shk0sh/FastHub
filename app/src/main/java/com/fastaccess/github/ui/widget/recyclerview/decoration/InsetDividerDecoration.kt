package com.fastaccess.github.ui.widget.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

/**
 * A decoration which draws a horizontal divider between [RecyclerView.ViewHolder]s of a given
 * type; with a left inset.
 * this class adopted from Plaid
 */
class InsetDividerDecoration constructor(
    private val height: Int,
    private val inset: Int,
    dividerColor: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        this.paint.color = dividerColor
        this.paint.style = Paint.Style.STROKE
        this.paint.strokeWidth = height.toFloat()
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount < 2) return
        val lm = parent.layoutManager ?: return
        val lines = FloatArray(childCount * 4)
        var hasDividers = false
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(child)
            val canDivide = viewHolder is HasDivider && viewHolder.canDivide()
            if (canDivide) {
                val position = parent.getChildAdapterPosition(child)
                if (child.isActivated || i + 1 < childCount && parent.getChildAt(i + 1).isActivated) {
                    continue
                }
                if (position != state.itemCount - 1) {
                    lines[i * 4] = (if (inset == 0) inset else inset + lm.getDecoratedLeft(child)).toFloat()
                    lines[i * 4 + 2] = lm.getDecoratedRight(child).toFloat()
                    val y = lm.getDecoratedBottom(child) + child.translationY.toInt() - height
                    lines[i * 4 + 1] = y.toFloat()
                    lines[i * 4 + 3] = y.toFloat()
                    hasDividers = true
                }
            }
        }
        if (hasDividers) {
            canvas.drawLines(lines, paint)
        }
    }

    interface HasDivider {
        fun canDivide(): Boolean
    }
}
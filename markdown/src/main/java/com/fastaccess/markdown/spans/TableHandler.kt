package com.fastaccess.markdown.spans

import android.graphics.*
import android.graphics.Paint.Style
import android.graphics.drawable.Drawable
import android.text.*
import android.text.Layout.Alignment
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode
import java.util.*

/**
 * Handles simple HTML tables.
 *
 *
 * Since it renders these tables itself, it needs to know things like font size
 * and text colour to use.
 *
 * @author Alex Kuiper
 */
class TableHandler(
        private val textColor: Int = Color.BLACK,
        private val tableWidth: Int = 500
) : TagNodeHandler() {
    private val textPaint: TextPaint
        get() {
            val textPaint = TextPaint()
            textPaint.color = this.textColor
            textPaint.linkColor = this.textColor
            textPaint.isAntiAlias = true
            textPaint.textSize = 28f
            textPaint.typeface = Typeface.DEFAULT
            return textPaint
        }

    override fun rendersContent() = true

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let {
            val table = getTable(node)
            for (i in 0 until table.rows.size) {
                val row = table.rows[i]
                builder.append("\uFFFC")
                val drawable = TableRowDrawable(row, table.isDrawBorder)
                drawable.setBounds(0, 0, drawable.intrinsicWidth,
                        drawable.intrinsicHeight)
                builder.setSpan(ImageSpan(drawable), start + i, builder.length, 33)

            }
            builder.append("\uFFFC")
            val drawable = TableRowDrawable(ArrayList(), table.isDrawBorder)
            drawable.setBounds(0, 0, tableWidth, 1)
            builder.setSpan(ImageSpan(drawable), builder.length - 1, builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan({ Alignment.ALIGN_CENTER } as AlignmentSpan, start, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.append("\n")
        }
    }

    private fun readNode(node: Any?, table: Table) {
        if (node is TagNode) {
            if (node.name == "td" || node.name == "th") {
                val result = this.spanner.fromTagNode(node, null)
                table.addCell(result)
                return
            }
            if (node.name == "tr") {
                table.addRow()
            }
            for (child in node.childTags) {
                readNode(child, table)
            }
        }

    }

    private fun getTable(node: TagNode?): Table {
        val border = node?.getAttributeByName("border")
        val drawBorder = "0" != border
        val result = Table(drawBorder)
        readNode(node, result)
        return result
    }

    private fun calculateRowHeight(row: List<Spanned>): Int {

        if (row.isEmpty()) {
            return 0
        }

        val textPaint = textPaint

        val columnWidth = tableWidth / row.size

        var rowHeight = 0

        for (cell in row) {

            val layout = StaticLayout(cell, textPaint, columnWidth - 2 * PADDING, Alignment.ALIGN_NORMAL, 1.5f, 0.5f, true)

            if (layout.height > rowHeight) {
                rowHeight = layout.height
            }
        }

        return rowHeight
    }

    private inner class TableRowDrawable internal constructor(private val tableRow: List<Spanned>, private val paintBorder: Boolean) : Drawable() {

        private val rowHeight: Int

        init {
            this.rowHeight = calculateRowHeight(tableRow)
        }

        override fun draw(canvas: Canvas) {
            val paint = Paint()
            paint.color = textColor
            paint.style = Style.STROKE

            val numberOfColumns = tableRow.size

            if (numberOfColumns == 0) {
                return
            }

            val columnWidth = tableWidth / numberOfColumns

            var offset: Int

            for (i in 0 until numberOfColumns) {

                offset = i * columnWidth

                if (paintBorder) {
                    // The rect is open at the bottom, so there's a single line
                    // between rows.
                    canvas.drawRect(offset.toFloat(), 0f, (offset + columnWidth).toFloat(), rowHeight.toFloat(), paint)
                }

                val layout = StaticLayout(tableRow[i],
                        textPaint, columnWidth - 2 * PADDING,
                        Alignment.ALIGN_NORMAL, 1.5f, 0.5f, true)

                canvas.translate((offset + PADDING).toFloat(), 0f)
                layout.draw(canvas)
                canvas.translate((-1 * (offset + PADDING)).toFloat(), 0f)

            }
        }

        override fun getIntrinsicHeight(): Int {
            return rowHeight
        }

        override fun getIntrinsicWidth(): Int {
            return tableWidth
        }

        override fun getOpacity(): Int {
            return PixelFormat.OPAQUE
        }

        override fun setAlpha(alpha: Int) {

        }

        override fun setColorFilter(cf: ColorFilter?) {

        }
    }

    private inner class Table constructor(internal val isDrawBorder: Boolean) {
        private val content = ArrayList<List<Spanned>>()

        internal val bottomRow: MutableList<Spanned>
            get() = content[content.size - 1] as MutableList<Spanned>

        internal val rows: List<List<Spanned>>
            get() = content

        internal fun addRow() {
            content.add(ArrayList())
        }

        internal fun addCell(text: Spanned) {
            if (content.isEmpty()) {
                throw IllegalStateException("No rows added yet")
            }

            bottomRow.add(text)
        }
    }

    companion object {
        private const val PADDING = 20
    }

}
package com.fastaccess.markdown

import android.content.Context
import android.graphics.RectF
import android.text.Selection
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView


/**
 * Created by Kosh on 23 Apr 2017, 12:09 PM
 *
 *
 * credit to to https://github.com/Saketme/Better-Link-Movement-Method
 */

class BetterLinkMovementExtended private constructor(context: Context) : LinkMovementMethod() {
    private var onLinkClickListener: ((link: String) -> Unit)? = null
    private var onLinkLongClickListener: ((link: String) -> Unit)? = null
    private val touchedLineBounds = RectF()
    private var isUrlHighlighted: Boolean = false
    private var touchStartedOverLink: Boolean = false
    private var activeTextViewHashcode: Int = 0

    private val gestureDetector: GestureDetector
    private val clickGestureListener = LinkClickGestureListener()

    init {
        gestureDetector = GestureDetector(context, clickGestureListener)
    }

    private inner class LinkClickGestureListener : GestureDetector.SimpleOnGestureListener() {
        var listener: GestureDetector.SimpleOnGestureListener? = null

        override fun onDown(e: MotionEvent): Boolean {
            listener?.onDown(e)
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return listener == null || listener?.onSingleTapUp(e) == true
        }

        override fun onLongPress(e: MotionEvent) {
            listener?.onLongPress(e)
        }
    }

    fun setOnLinkClickListener(onLinkClickListener: ((link: String) -> Unit)? = null) {
        this.onLinkClickListener = onLinkClickListener
    }

    fun setOnLinkLongClickListener(onLinkClickListener: ((link: String) -> Unit)? = null) {
        this.onLinkLongClickListener = onLinkClickListener
    }

    override fun onTouchEvent(view: TextView, text: Spannable, event: MotionEvent): Boolean {
        if (this.activeTextViewHashcode != view.hashCode()) {
            this.activeTextViewHashcode = view.hashCode()
            view.autoLinkMask = 0
        }

        val touchedClickableSpan = this.findClickableSpanUnderTouch(view, text, event)
        if (touchedClickableSpan != null) {
            this.highlightUrl(view, touchedClickableSpan, text)
        } else {
            this.removeUrlHighlightColor(view)
        }

        clickGestureListener.listener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                touchStartedOverLink = touchedClickableSpan != null
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (touchedClickableSpan != null && touchStartedOverLink) {
                    dispatchUrlClick(view, touchedClickableSpan)
                    removeUrlHighlightColor(view)
                }

                touchStartedOverLink = false
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                if (touchedClickableSpan != null && touchStartedOverLink) {
                    dispatchUrlLongClick(view, touchedClickableSpan)
                    removeUrlHighlightColor(view)
                }

                touchStartedOverLink = false
            }
        }

        var ret = gestureDetector.onTouchEvent(event)

        if (!ret && event.action == MotionEvent.ACTION_UP) {
            clickGestureListener.listener = null
            removeUrlHighlightColor(view)
            this.touchStartedOverLink = false
            ret = true
        }

        return ret
    }

    private fun findClickableSpanUnderTouch(textView: TextView, text: Spannable,
                                            event: MotionEvent): BetterLinkMovementExtended.ClickableSpanWithText? {
        var touchX = event.x.toInt()
        var touchY = event.y.toInt()
        touchX -= textView.totalPaddingLeft
        touchY -= textView.totalPaddingTop
        touchX += textView.scrollX
        touchY += textView.scrollY
        val layout = textView.layout
        val touchedLine = layout.getLineForVertical(touchY)
        val touchOffset = layout.getOffsetForHorizontal(touchedLine, touchX.toFloat())
        this.touchedLineBounds.left = layout.getLineLeft(touchedLine)
        this.touchedLineBounds.top = layout.getLineTop(touchedLine).toFloat()
        this.touchedLineBounds.right = layout.getLineWidth(touchedLine) + this.touchedLineBounds.left
        this.touchedLineBounds.bottom = layout.getLineBottom(touchedLine).toFloat()
        if (this.touchedLineBounds.contains(touchX.toFloat(), touchY.toFloat())) {
            val spans = text.getSpans<ClickableSpan>(touchOffset, touchOffset, SPAN_CLASS)
            for (span in spans) {
                if (span is ClickableSpan) {
                    return ClickableSpanWithText.ofSpan(textView, span)
                }
            }
            return null
        } else {
            return null
        }
    }

    private fun highlightUrl(textView: TextView, spanWithText: BetterLinkMovementExtended.ClickableSpanWithText?, text: Spannable) {
        if (!this.isUrlHighlighted) {
            this.isUrlHighlighted = true
            val spanStart = text.getSpanStart(spanWithText!!.span())
            val spanEnd = text.getSpanEnd(spanWithText.span())
            Selection.removeSelection(text)
            text.setSpan(BackgroundColorSpan(textView.highlightColor), spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            textView.text = text
            Selection.setSelection(text, spanStart, spanEnd)
        }
    }

    private fun removeUrlHighlightColor(textView: TextView) {
        if (this.isUrlHighlighted) {
            this.isUrlHighlighted = false
            val text = textView.text as Spannable
            val highlightSpans = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
            for (highlightSpan in highlightSpans) {
                text.removeSpan(highlightSpan)
            }
            try {
                textView.text = text
                Selection.removeSelection(text)
            } catch (ignored: Exception) {
            }

        }
    }

    private fun dispatchUrlClick(textView: TextView, spanWithText: BetterLinkMovementExtended.ClickableSpanWithText) {
        val spanUrl = spanWithText.text()
        val handled = this.onLinkClickListener != null
        if (!handled) {
            spanWithText.span().onClick(textView)
        } else {
            this.onLinkClickListener?.invoke(spanUrl)
        }

    }

    private fun dispatchUrlLongClick(textView: TextView, spanWithText: BetterLinkMovementExtended.ClickableSpanWithText) {
        val spanUrl = spanWithText.text()
        if (onLinkLongClickListener != null) this.onLinkLongClickListener?.invoke(spanUrl)
    }

    internal class ClickableSpanWithText private constructor(private val span: ClickableSpan, private val text: String) {

        fun span(): ClickableSpan {
            return this.span
        }

        fun text(): String {
            return this.text
        }

        companion object {

            fun ofSpan(textView: TextView, span: ClickableSpan): BetterLinkMovementExtended.ClickableSpanWithText {
                val s = textView.text as Spanned
                val text: String
                if (span is URLSpan) {
                    text = span.url
                } else {
                    val start = s.getSpanStart(span)
                    val end = s.getSpanEnd(span)
                    text = s.subSequence(start, end).toString()
                }
                return BetterLinkMovementExtended.ClickableSpanWithText(span, text)
            }
        }
    }

    companion object {
        private val SPAN_CLASS = ClickableSpan::class.java
        private const val LINKIFY_NONE = -2

        private fun linkify(linkifyMask: Int, textView: TextView): BetterLinkMovementExtended {
            val movementMethod = BetterLinkMovementExtended(textView.context)
            addLinks(linkifyMask, movementMethod, textView)
            return movementMethod
        }

        fun linkifyHtml(textView: TextView): BetterLinkMovementExtended {
            return linkify(LINKIFY_NONE, textView)
        }

        private fun linkify(linkifyMask: Int, viewGroup: ViewGroup): BetterLinkMovementExtended {
            val movementMethod = BetterLinkMovementExtended(viewGroup.context)
            rAddLinks(linkifyMask, viewGroup, movementMethod)
            return movementMethod
        }

        fun linkifyHtml(viewGroup: ViewGroup): BetterLinkMovementExtended {
            return linkify(LINKIFY_NONE, viewGroup)
        }

        private fun rAddLinks(linkifyMask: Int, viewGroup: ViewGroup, movementMethod: BetterLinkMovementExtended) {
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                if (child is ViewGroup) {
                    rAddLinks(linkifyMask, child, movementMethod)
                } else if (child is TextView) {
                    addLinks(linkifyMask, movementMethod, child)
                }
            }

        }

        private fun addLinks(linkifyMask: Int, movementMethod: BetterLinkMovementExtended, textView: TextView) {
            textView.movementMethod = movementMethod
            if (linkifyMask != LINKIFY_NONE) {
                Linkify.addLinks(textView, linkifyMask)
            }

        }
    }
}
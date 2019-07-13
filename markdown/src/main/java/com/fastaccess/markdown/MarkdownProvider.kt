package com.fastaccess.markdown

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import com.fastaccess.github.extensions.generateTextColor
import com.fastaccess.markdown.spans.DrawableHandler
import com.fastaccess.markdown.spans.HrHandler
import com.fastaccess.markdown.spans.PreTagHandler
import com.fastaccess.markdown.spans.QuoteHandler
import net.nightwhistler.htmlspanner.HtmlSpanner
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Created by Kosh on 02.02.19.
 */
object MarkdownProvider {

    private const val TOGGLE_START = "<span class=\"email-hidden-toggle\">"
    private const val TOGGLE_END = "</span>"
    private const val REPLY_START = "<div class=\"email-quoted-reply\">"
    private const val REPLY_END = "</div>"
    private const val SIGNATURE_START = "<div class=\"email-signature-reply\">"
    private const val SIGNATURE_END = "</div>"
    private const val HIDDEN_REPLY_START = "<div class=\"email-hidden-reply\" style=\" display:none\">"
    private const val HIDDEN_REPLY_END = "</div>"
    private const val BREAK = "<br>"
    private const val PARAGRAPH_START = "<p>"
    private const val PARAGRAPH_END = "</p>"

    fun loadIntoTextView(
        htmlSpanner: HtmlSpanner,
        textView: TextView,
        html: String,
        windowBackground: Int,
        isLightTheme: Boolean,
        onLinkClicked: ((link: String) -> Unit)? = null
    ) {
        val width = textView.measuredWidth
        if (width > 0) {
            initTextView(width, htmlSpanner, textView, html, windowBackground, isLightTheme, onLinkClicked)
        } else {
            textView.doOnPreDraw {
                initTextView(textView.width, htmlSpanner, textView, html, windowBackground, isLightTheme, onLinkClicked)
            }
        }
    }

    fun stripHtml(text: String): String {
        val parser = Parser.builder().build()
        val node = parser.parse(text)
        return HtmlCompat.fromHtml(HtmlRenderer.builder().build().render(node), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    fun stripMd(text: String) = stripHtml(text)

    private fun initTextView(
        width: Int,
        htmlSpanner: HtmlSpanner,
        textView: TextView,
        html: String,
        windowBackground: Int,
        isLightTheme: Boolean,
        onLinkClicked: ((link: String) -> Unit)? = null
    ) {
        val linkMovementMethod = LinkMovementMethod.getInstance()
        textView.movementMethod =  linkMovementMethod
        htmlSpanner.registerHandler("pre", PreTagHandler(windowBackground, true, isLightTheme))
        htmlSpanner.registerHandler("code", PreTagHandler(windowBackground, false, isLightTheme))
        htmlSpanner.registerHandler("img", DrawableHandler(textView, width))
        htmlSpanner.registerHandler("blockquote", QuoteHandler(windowBackground))
        htmlSpanner.registerHandler("hr", HrHandler(windowBackground, width))
        val tableHandler = net.nightwhistler.htmlspanner.handlers.TableHandler()
        tableHandler.setTableWidth(width)
        tableHandler.setTextSize(20f)
        tableHandler.setTextColor(windowBackground.generateTextColor())
        htmlSpanner.registerHandler("table", tableHandler)
        textView.text = htmlSpanner.fromHtml(format(html).toString())
    }

    //https://github.com/k0shk0sh/GitHubSdk/blob/master/library/src/main/java/com/meisolsson/githubsdk/core/HtmlUtils.java
    private fun format(html: String): CharSequence {
        if (html.isEmpty()) return ""
        val formatted = StringBuilder(html)
        strip(formatted, TOGGLE_START, TOGGLE_END)
        strip(formatted, SIGNATURE_START, SIGNATURE_END)
        strip(formatted, REPLY_START, REPLY_END)
        strip(formatted, HIDDEN_REPLY_START, HIDDEN_REPLY_END)
        if (replace(formatted, PARAGRAPH_START, BREAK)) replace(formatted, PARAGRAPH_END, BREAK)
        trim(formatted)
        return formatted
    }

    private fun strip(input: StringBuilder, prefix: String, suffix: String) {
        var start = input.indexOf(prefix)
        while (start != -1) {
            var end = input.indexOf(suffix, start + prefix.length)
            if (end == -1)
                end = input.length
            input.delete(start, end + suffix.length)
            start = input.indexOf(prefix, start)
        }
    }

    private fun replace(input: StringBuilder, from: String, to: String): Boolean {
        var start = input.indexOf(from)
        if (start == -1) return false
        val fromLength = from.length
        val toLength = to.length
        while (start != -1) {
            input.replace(start, start + fromLength, to)
            start = input.indexOf(from, start + toLength)
        }
        return true
    }

    private fun trim(input: StringBuilder) {
        var length = input.length
        val breakLength = BREAK.length
        while (length > 0) {
            if (input.indexOf(BREAK) == 0)
                input.delete(0, breakLength)
            else if (length >= breakLength && input.lastIndexOf(BREAK) == length - breakLength)
                input.delete(length - breakLength, length)
            else if (Character.isWhitespace(input[0]))
                input.deleteCharAt(0)
            else if (Character.isWhitespace(input[length - 1]))
                input.deleteCharAt(length - 1)
            else
                break
            length = input.length
        }
    }
}
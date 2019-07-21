package com.fastaccess.markdown

import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import io.noties.markwon.Markwon
import io.noties.prism4j.annotations.PrismBundle
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer


/**
 * Created by Kosh on 02.02.19.
 */
@PrismBundle(includeAll = true)
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
        markwon: Markwon,
        textView: TextView,
        html: String,
        windowBackground: Int,
        isLightTheme: Boolean,
        onLinkClicked: ((link: String) -> Unit)? = null
    ) {
        val width = textView.measuredWidth
        if (width > 0) {
            initTextView(width, markwon, textView, html, windowBackground, isLightTheme, onLinkClicked)
        } else {
            textView.doOnPreDraw {
                initTextView(textView.width, markwon, textView, html, windowBackground, isLightTheme, onLinkClicked)
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
        markwon: Markwon,
        textView: TextView,
        html: String,
        windowBackground: Int,
        isLightTheme: Boolean,
        onLinkClicked: ((link: String) -> Unit)? = null
    ) {
        markwon.setMarkdown(textView, html)
    }
}
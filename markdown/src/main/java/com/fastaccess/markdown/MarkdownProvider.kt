package com.fastaccess.markdown

import android.webkit.MimeTypeMap
import android.widget.EditText
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

    private val IMAGE_EXTENSIONS = arrayOf(".png", ".jpg", ".jpeg", ".gif", ".svg")

    private val MARKDOWN_EXTENSIONS = arrayOf(".md", ".mkdn", ".mdwn", ".mdown", ".markdown", ".mkd", ".mkdown", ".ron", ".rst", "adoc")

    private val ARCHIVE_EXTENSIONS = arrayOf(
        ".zip", ".7z", ".rar", ".tar.gz", ".tgz", ".tar.Z", ".tar.bz2", ".tbz2", ".tar.lzma", ".tlz", ".apk", ".jar", ".dmg", ".pdf", ".ico", ".docx",
        ".doc", ".xlsx", ".hwp", ".pptx", ".show", ".mp3", ".ogg", ".ipynb"
    )

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

    fun addList(
        editText: EditText,
        list: String
    ) {
        val tag = "$list "
        val source = editText.text.toString()
        var selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        var substring = source.substring(0, selectionStart)
        val line = substring.lastIndexOf(char = 10.toChar())
        if (line != -1) {
            selectionStart = line + 1
        } else {
            selectionStart = 0
        }
        substring = source.substring(selectionStart, selectionEnd)
        val split = substring.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringBuffer = StringBuilder()
        if (split.isNotEmpty())
            for (s in split) {
                if (s.isEmpty() && stringBuffer.isNotEmpty()) {
                    stringBuffer.append("\n")
                    continue
                }
                if (!s.trim { it <= ' ' }.startsWith(tag)) {
                    if (stringBuffer.isNotEmpty()) stringBuffer.append("\n")
                    stringBuffer.append(tag).append(s)
                } else {
                    if (stringBuffer.isNotEmpty()) stringBuffer.append("\n")
                    stringBuffer.append(s)
                }
            }

        if (stringBuffer.isEmpty()) {
            stringBuffer.append(tag)
        }
        editText.text.replace(selectionStart, selectionEnd, stringBuffer.toString())
        editText.setSelection(stringBuffer.length + selectionStart)
    }

    fun addHeader(
        editText: EditText,
        level: Int
    ) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val result = StringBuilder()
        val substring = source.substring(selectionStart, selectionEnd)
        if (!hasNewLine(source, selectionStart)) result.append("\n")
        for (i in 0..level) result.append("#")
        result.append(" ").append(substring)
        editText.text.replace(selectionStart, selectionEnd, result.toString())
        editText.setSelection(selectionStart + result.length)

    }

    fun addItalic(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "_" + substring + "_ "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 2)

    }

    fun addBold(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "**$substring** "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 3)

    }

    fun addCode(editText: EditText) {
        try {
            val source = editText.text.toString()
            val selectionStart = editText.selectionStart
            val selectionEnd = editText.selectionEnd
            val substring = source.substring(selectionStart, selectionEnd)
            val result: String
            result = if (hasNewLine(source, selectionStart))
                "```\n$substring\n```\n"
            else
                "\n```\n$substring\n```\n"

            editText.text.replace(selectionStart, selectionEnd, result)
            editText.setSelection(result.length + selectionStart - 5)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun addInlinleCode(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "`$substring` "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 2)

    }

    fun addStrikeThrough(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result = "~~$substring~~ "
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart - 3)

    }

    fun addQuote(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        val substring = source.substring(selectionStart, selectionEnd)
        val result: String
        result = if (hasNewLine(source, selectionStart)) {
            "> $substring"
        } else {
            "\n> $substring"

        }
        editText.text.replace(selectionStart, selectionEnd, result)
        editText.setSelection(result.length + selectionStart)

    }

    fun addDivider(editText: EditText) {
        val source = editText.text.toString()
        val selectionStart = editText.selectionStart
        val result: String
        result = if (hasNewLine(source, selectionStart)) {
            "-------\n"
        } else {
            "\n-------\n"
        }
        editText.text.replace(selectionStart, selectionStart, result)
        editText.setSelection(result.length + selectionStart)

    }

    fun addPhoto(
        editText: EditText,
        title: String,
        link: String
    ) {
        val result = "![$title]($link)"
        insertAtCursor(editText, result)
    }

    fun addLink(
        editText: EditText,
        title: String,
        link: String
    ) {
        val result = "[$title]($link)"
        insertAtCursor(editText, result)
    }

    private fun hasNewLine(
        source: String,
        selectionStart: Int
    ): Boolean {
        var _source = source
        try {
            if (_source.isEmpty()) return true
            _source = _source.substring(0, selectionStart)
            return _source[_source.length - 1].toInt() == 10
        } catch (e: StringIndexOutOfBoundsException) {
            return false
        }
    }

    fun isImage(name: String?): Boolean {
        var name = name
        if (name.isNullOrEmpty()) return false
        name = name.toLowerCase()
        for (value in IMAGE_EXTENSIONS) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(name)
            if (extension != null && value.replace(".", "") == extension || name.endsWith(value)) return true
        }
        return false
    }

    fun isMarkdown(name: String?): Boolean {
        var _name = name
        if (_name.isNullOrEmpty()) return false
        _name = _name.toLowerCase()
        for (value in MARKDOWN_EXTENSIONS) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(_name)
            if (extension != null && value.replace(".", "") == extension ||
                _name.equals("README", ignoreCase = true) || _name.endsWith(value)
            )
                return true
        }
        return false
    }

    fun isArchive(name: String?): Boolean {
        var _name = name
        if (_name.isNullOrEmpty()) return false
        _name = _name.toLowerCase()
        for (value in ARCHIVE_EXTENSIONS) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(_name)
            if (extension != null && value.replace(".", "") == extension || _name.endsWith(value)) return true
        }

        return false
    }

    fun insertAtCursor(
        editText: EditText,
        text: String
    ) {
        val oriContent = editText.text.toString()
        val start = editText.selectionStart
        val end = editText.selectionEnd
        if (start >= 0 && end > 0 && start != end) {
            editText.text = editText.text.replace(start, end, text)
        } else {
            val index = if (editText.selectionStart >= 0) editText.selectionStart else 0
            val builder = StringBuilder(oriContent)
            builder.insert(index, text)
            editText.setText(builder.toString())
            editText.setSelection(index + text.length)
        }
    }
}
package com.fastaccess.markdown.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.R
import kotlinx.android.synthetic.main.markdown_buttons_layout.view.*

/**
 * Created by Kosh on 2019-07-22.
 */
class MarkdownLayout : LinearLayout, View.OnClickListener {

    lateinit var layoutCallback: MarkdownLayoutCallback


    constructor(context: Context?) : super(context)
    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        orientation = HORIZONTAL
        View.inflate(context, R.layout.markdown_buttons_layout, this)
        if (isInEditMode) return
    }

    fun init() {
        headerOne.setOnClickListener(this)
        headerTwo.setOnClickListener(this)
        headerThree.setOnClickListener(this)
        bold.setOnClickListener(this)
        italic.setOnClickListener(this)
        strikethrough.setOnClickListener(this)
        bullet.setOnClickListener(this)
        header.setOnClickListener(this)
        code.setOnClickListener(this)
        numbered.setOnClickListener(this)
        quote.setOnClickListener(this)
        link.setOnClickListener(this)
        image.setOnClickListener(this)
        unCheckbox.setOnClickListener(this)
        checkbox.setOnClickListener(this)
        inlineCode.setOnClickListener(this)
        addEmoji.setOnClickListener(this)
        signature.setOnClickListener(this)
        view.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val editText = layoutCallback.provideEditText()
        if (editText.selectionEnd == -1 || editText.selectionStart == -1) {
            return
        }
        v?.let {
            when (it.id) {
                R.id.headerOne -> MarkdownProvider.addHeader(editText, 1)
                R.id.headerTwo -> MarkdownProvider.addHeader(editText, 2)
                R.id.headerThree -> MarkdownProvider.addHeader(editText, 3)
                R.id.bold -> MarkdownProvider.addBold(editText)
                R.id.italic -> MarkdownProvider.addItalic(editText)
                R.id.strikethrough -> MarkdownProvider.addStrikeThrough(editText)
                R.id.bullet -> MarkdownProvider.addList(editText, "-")
                R.id.header -> MarkdownProvider.addDivider(editText)
                R.id.code -> MarkdownProvider.addCode(editText)
                R.id.numbered -> MarkdownProvider.addList(editText, "1")
                R.id.quote -> MarkdownProvider.addQuote(editText)
                R.id.link -> layoutCallback.openLinkDialog()
                R.id.image -> layoutCallback.openLinkDialog(true)
                R.id.unCheckbox -> MarkdownProvider.addList(editText, "- [x]")
                R.id.checkbox -> MarkdownProvider.addList(editText, "- [ ]")
                R.id.inlineCode -> MarkdownProvider.addInlinleCode(editText)
                R.id.view -> {
                    val isPreview = editText.isVisible
                    editText.isVisible = !isPreview
                    layoutCallback.provideReview(isPreview)
                }
            }
        }
    }

    fun onLinkSelected(
        title: String,
        link: String,
        isImage: Boolean
    ) {
        val editText = layoutCallback.provideEditText()
        if (isImage) {
            MarkdownProvider.addPhoto(editText, title, link)
        } else {
            MarkdownProvider.addLink(editText, title, link)
        }
    }

    interface MarkdownLayoutCallback {
        fun provideEditText(): EditText
        fun provideReview(isReview: Boolean)
        fun openLinkDialog(isImage: Boolean = false)
    }
}
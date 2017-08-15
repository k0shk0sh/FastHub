package com.fastaccess.ui.widgets.markdown

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.transition.TransitionManager
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.markdown.MarkDownProvider
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageDialogFragment

/**
 * Created by kosh on 11/08/2017.
 */
class MarkDownLayout : LinearLayout {

    var markdownListener: MarkdownListener? = null

    @BindView(R.id.editorIconsHolder) lateinit var editorIconsHolder: HorizontalScrollView

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.markdown_buttons_layout, this)
        if (isInEditMode) return
        ButterKnife.bind(this)
    }

    override fun onDetachedFromWindow() {
        markdownListener = null
        super.onDetachedFromWindow()
    }

    @OnClick(R.id.view) internal fun onViewMarkDown() {
        markdownListener?.let {
            it.getEditText().let { editText ->
                TransitionManager.beginDelayedTransition(this)
                if (editText.isEnabled && !InputHelper.isEmpty(editText)) {
                    editText.isEnabled = false
                    it.onReview(false)
                    MarkDownProvider.setMdText(editText, InputHelper.toString(editText))
                    editorIconsHolder.visibility = View.INVISIBLE
                    ViewHelper.hideKeyboard(editText)
                } else {
                    editText.setText(it.getSavedText())
                    editText.setSelection(it.getSavedText().length)
                    editText.isEnabled = true
                    it.onReview(true)
                    editorIconsHolder.visibility = View.VISIBLE
                    ViewHelper.showKeyboard(editText)
                }
            }
        }
    }

    @OnClick(R.id.headerOne, R.id.headerTwo, R.id.headerThree, R.id.bold, R.id.italic, R.id.strikethrough,
            R.id.bullet, R.id.header, R.id.code, R.id.numbered, R.id.quote, R.id.link, R.id.image,
            R.id.unCheckbox, R.id.checkbox, R.id.inlineCode)
    fun onActions(v: View) {
        markdownListener?.let {
            it.getEditText().let { editText ->
                if (!editText.isEnabled) {
                    Snackbar.make(this, R.string.error_highlighting_editor, Snackbar.LENGTH_SHORT).show()
                } else {
                    if (v.id == R.id.link) {
                        EditorLinkImageDialogFragment.newInstance(true).show(it.fragmentManager(), "BannerDialogFragment")
                    } else if (v.id == R.id.image) {
                        EditorLinkImageDialogFragment.newInstance(false).show(it.fragmentManager(), "BannerDialogFragment")
                    } else {
                        onActionClicked(editText, v.id)
                    }
                }
            }
        }
    }

    private fun onActionClicked(editText: EditText, id: Int) {
        if (editText.selectionEnd == -1 || editText.selectionStart == -1) {
            return
        }
        when (id) {
            R.id.headerOne -> MarkDownProvider.addHeader(editText, 1)
            R.id.headerTwo -> MarkDownProvider.addHeader(editText, 2)
            R.id.headerThree -> MarkDownProvider.addHeader(editText, 3)
            R.id.bold -> MarkDownProvider.addBold(editText)
            R.id.italic -> MarkDownProvider.addItalic(editText)
            R.id.strikethrough -> MarkDownProvider.addStrikeThrough(editText)
            R.id.numbered -> MarkDownProvider.addList(editText, "1.")
            R.id.bullet -> MarkDownProvider.addList(editText, "-")
            R.id.header -> MarkDownProvider.addDivider(editText)
            R.id.code -> MarkDownProvider.addCode(editText)
            R.id.quote -> MarkDownProvider.addQuote(editText)
            R.id.link -> MarkDownProvider.addLink(editText)
            R.id.image -> MarkDownProvider.addPhoto(editText)
            R.id.checkbox -> MarkDownProvider.addList(editText, "- [x]")
            R.id.unCheckbox -> MarkDownProvider.addList(editText, "- [ ]")
            R.id.inlineCode -> MarkDownProvider.addInlinleCode(editText)
        }
    }

    interface MarkdownListener {
        fun getEditText(): EditText
        fun fragmentManager(): FragmentManager
        fun getSavedText(): CharSequence
        fun onReview(enabled: Boolean) {}
    }
}
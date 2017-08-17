package com.fastaccess.ui.modules.editor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.transition.TransitionManager
import android.support.v4.app.FragmentManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import butterknife.BindView
import butterknife.OnClick
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.EditReviewCommentModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.helper.*
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.provider.markdown.CachedComments
import com.fastaccess.provider.markdown.MarkDownProvider
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText
import java.util.*

/**
 * Created by Kosh on 27 Nov 2016, 1:32 AM
 */

class EditorActivity : BaseActivity<EditorMvp.View, EditorPresenter>(), EditorMvp.View {

    private val sentFromFastHub: String by lazy {
        "\n\n_" + getString(R.string.sent_from_fasthub, AppHelper.getDeviceName(), "",
                "[" + getString(R.string.app_name) + "](https://play.google.com/store/apps/details?id=com.fastaccess.github)") + "_"
    }

    private var participants: ArrayList<String>? = null

    @BindView(R.id.replyQuote) lateinit var replyQuote: LinearLayout
    @BindView(R.id.replyQuoteText) lateinit var quote: FontTextView
    @BindView(R.id.markDownLayout) lateinit var markDownLayout: MarkDownLayout
    @BindView(R.id.editText) lateinit var editText: MarkdownEditText
    @BindView(R.id.sentVia) lateinit var sentVia: CheckBox
    @BindView(R.id.list_divider) lateinit var listDivider: View
    @BindView(R.id.parentView) lateinit var parentView: View
    @BindView(R.id.autocomplete) lateinit var mention: ListView

    @State
    @BundleConstant.ExtraTYpe
    var extraType: String? = null
    @State
    var itemId: String? = null
    @State
    var login: String? = null
    @State
    var issueNumber: Int = 0
    @State
    var commentId: Long = 0
    @State
    var sha: String? = null
    @State
    var reviewComment: EditReviewCommentModel? = null

    override fun layout(): Int = R.layout.editor_layout

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): EditorPresenter = EditorPresenter()

    @OnClick(R.id.replyQuoteText) internal fun onToggleQuote() {
        TransitionManager.beginDelayedTransition((parentView as ViewGroup))
        if (quote.maxLines == 3) {
            quote.maxLines = Integer.MAX_VALUE
        } else {
            quote.maxLines = 3
        }
        quote.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                if (quote.maxLines == 3) R.drawable.ic_arrow_drop_down
                else R.drawable.ic_arrow_drop_up, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        markDownLayout.markdownListener = this
        setToolbarIcon(R.drawable.ic_clear)
        sentVia.visibility = if (PrefGetter.isSentViaBoxEnabled()) View.VISIBLE else GONE
        sentVia.isChecked = PrefGetter.isSentViaEnabled()
        sentVia.setOnCheckedChangeListener { _, isChecked -> PrefHelper.set("sent_via", isChecked) }
        MarkDownProvider.setMdText(sentVia, sentFromFastHub)
        if (savedInstanceState == null) {
            onCreate()
        }
        invalidateOptionsMenu()
        editText.initListView(mention, listDivider, participants)
        if (editText.text.toString().contains(sentFromFastHub)) {
            sentVia.isChecked = true
        }
        editText.requestFocus()
    }

    override fun onSendResultAndFinish(commentModel: Comment, isNew: Boolean) {
        hideProgress()
        val intent = Intent()
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, commentModel)
                .put(BundleConstant.EXTRA, isNew)
                .end())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onSendMarkDownResult() {
        val intent = Intent()
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, editText.savedText).end())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onSendReviewResultAndFinish(comment: EditReviewCommentModel, isNew: Boolean) {
        hideProgress()
        val intent = Intent()
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ITEM, comment)
                .put(BundleConstant.EXTRA, isNew)
                .end())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            if (PrefGetter.isSentViaEnabled()) {
                val temp = editText.savedText.toString()
                if (!temp.contains(sentFromFastHub)) {
                    editText.savedText = editText.savedText.toString() + sentFromFastHub
                }
            }
            presenter.onHandleSubmission(editText.savedText, extraType, itemId, commentId, login, issueNumber, sha, reviewComment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (menu.findItem(R.id.submit) != null) {
            menu.findItem(R.id.submit).isEnabled = true
        }
        if (BundleConstant.ExtraTYpe.FOR_RESULT_EXTRA.equals(extraType, ignoreCase = true)) {
            menu.findItem(R.id.submit).setIcon(R.drawable.ic_done)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun showProgress(@StringRes resId: Int) {
        super.showProgress(resId)
        invalidateOptionsMenu()
    }

    override fun hideProgress() {
        invalidateOptionsMenu()
        super.hideProgress()
    }

    override fun onBackPressed() {
        if (!InputHelper.isEmpty(editText)) {
            ViewHelper.hideKeyboard(editText)
            CachedComments.instance.put(itemId, login, issueNumber, editText.savedText)
        }
        super.onBackPressed()
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk && bundle != null) {
            finish()
        }
    }

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        if (isLink) {
            MarkDownProvider.addLink(editText, InputHelper.toString(title), InputHelper.toString(link))
        } else {
            editText.setText(String.format("%s\n", editText.text))
            MarkDownProvider.addPhoto(editText, InputHelper.toString(title), InputHelper.toString(link))
        }
    }

    override fun getEditText(): EditText = editText

    override fun getSavedText(): CharSequence = editText.savedText

    override fun onReview(enabled: Boolean) {
        sentVia.isEnabled = enabled
    }

    override fun fragmentManager(): FragmentManager = supportFragmentManager

    @SuppressLint("SetTextI18n")
    override fun onEmojiAdded(emoji: Emoji) {
        ViewHelper.showKeyboard(editText)
        editText.setText("${editText.text} :${emoji.aliases[0]}:")
    }

    private fun onCreate() {
        val intent = intent
        if (intent != null && intent.extras != null) {
            val bundle = intent.extras
            extraType = bundle.getString(BundleConstant.EXTRA_TYPE)
            reviewComment = bundle.getParcelable<EditReviewCommentModel>(BundleConstant.REVIEW_EXTRA)
            itemId = bundle.getString(BundleConstant.ID)
            login = bundle.getString(BundleConstant.EXTRA_TWO)
            if (extraType.equals(BundleConstant.ExtraTYpe.EDIT_COMMIT_COMMENT_EXTRA, ignoreCase = true)
                    || extraType.equals(BundleConstant.ExtraTYpe.NEW_COMMIT_COMMENT_EXTRA, ignoreCase = true)) {
                sha = bundle.getString(BundleConstant.EXTRA_THREE)
            } else {
                issueNumber = bundle.getInt(BundleConstant.EXTRA_THREE)
            }
            commentId = bundle.getLong(BundleConstant.EXTRA_FOUR)
            val textToUpdate = bundle.getString(BundleConstant.EXTRA)
            if (!InputHelper.isEmpty(textToUpdate)) {
                editText.setText(String.format("%s ", textToUpdate))
                editText.setSelection(InputHelper.toString(editText).length)
            }
            if (bundle.getString("message", "").isEmpty())
                replyQuote.visibility = GONE
            else {
                MarkDownProvider.setMdText(quote, bundle.getString("message", ""))
            }
            participants = bundle.getStringArrayList("participants")
        }
        if (InputHelper.isEmpty(editText)) {
            editText.setText(CachedComments.instance.get(itemId, login, issueNumber))
        }
    }
}
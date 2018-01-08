package com.fastaccess.ui.modules.editor.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.editor.EditorActivity
import com.fastaccess.ui.modules.editor.emoji.EmojiMvp
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar

/**
 * Created by kosh on 21/08/2017.
 */
class CommentEditorFragment : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(), MarkDownLayout.MarkdownListener,
        EmojiMvp.EmojiCallback, EditorLinkImageMvp.EditorLinkCallback {

    @BindView(R.id.commentBox) lateinit var commentBox: View
    @BindView(R.id.markdDownLayout) lateinit var markdDownLayout: MarkDownLayout
    @BindView(R.id.commentText) lateinit var commentText: MarkdownEditText
    @BindView(R.id.markdownBtnHolder) lateinit var markdownBtnHolder: View
    @BindView(R.id.sendComment) lateinit var sendComment: View
    @BindView(R.id.toggleButtons) lateinit var toggleButtons: View
    private var commentListener: CommentListener? = null
    private var keyboardListener: Unregistrar? = null

    @OnClick(R.id.sendComment) internal fun onComment() {
        if (!InputHelper.isEmpty(getEditText())) {
            commentListener?.onSendActionClicked(InputHelper.toString(getEditText()), arguments?.getBundle(BundleConstant.ITEM))
            ViewHelper.hideKeyboard(getEditText())
            arguments = null
        }
    }

    @OnClick(R.id.fullScreenComment) internal fun onExpandScreen() {
        val intent = Intent(context, EditorActivity::class.java)
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.FOR_RESULT_EXTRA)
                .put(BundleConstant.EXTRA, getEditText().text.toString())
                .putStringArrayList("participants", commentListener?.getNamesToTag())
                .end())
        startActivityForResult(intent, BundleConstant.REQUEST_CODE)
    }

    @OnClick(R.id.toggleButtons) internal fun onToggleButtons(v: View) {
        TransitionManager.beginDelayedTransition((view as ViewGroup?)!!)
        v.isActivated = !v.isActivated
        markdownBtnHolder.visibility = if (markdownBtnHolder.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (parentFragment is CommentListener) {
            commentListener = parentFragment as CommentListener
        } else if (context is CommentListener) {
            commentListener = context
        }
    }

    override fun onDetach() {
        commentListener = null
        super.onDetach()
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.comment_box_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            val hideSendButton = it.getBoolean(BundleConstant.YES_NO_EXTRA)
            if (hideSendButton) {
                sendComment.visibility = View.GONE
            }
        }
        markdDownLayout.markdownListener = this
        if (savedInstanceState == null) {
            arguments?.getBundle(BundleConstant.ITEM)?.getString(BundleConstant.EXTRA)?.let { commentText.setText(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        keyboardListener = KeyboardVisibilityEvent.registerEventListener(activity, {
            TransitionManager.beginDelayedTransition((view as ViewGroup?)!!)
            toggleButtons.isActivated = it
            markdownBtnHolder.visibility = if (!it) View.GONE else View.VISIBLE
        })
    }

    override fun onStop() {
        keyboardListener?.unregister()
        super.onStop()
    }

    override fun getEditText(): EditText = commentText

    override fun fragmentManager(): FragmentManager = childFragmentManager

    override fun getSavedText(): CharSequence? = commentText.savedText

    override fun onEmojiAdded(emoji: Emoji?) = markdDownLayout.onEmojiAdded(emoji)

    @SuppressLint("SetTextI18n")
    fun onCreateComment(text: String, bundle: Bundle?) {
        arguments = Bundler.start().put(BundleConstant.ITEM, bundle).end()
        commentText.setText("${if (commentText.text.isNullOrBlank()) "" else "${commentText.text} "}$text")
        getEditText().setSelection(getEditText().text.length)
        commentText.requestFocus()
        ViewHelper.showKeyboard(commentText)
    }

    fun onAddUserName(username: String) {
        getEditText().setText(if (getEditText().text.isNullOrBlank()) {
            "@$username"
        } else {
            "${getEditText().text} @$username"
        })
        getEditText().setSelection(getEditText().text.length)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BundleConstant.REQUEST_CODE) {
                val text = data?.extras?.getCharSequence(BundleConstant.EXTRA)
                getEditText().setText(text)
                getEditText().setSelection(getEditText().text.length)
            }
        }
    }

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markdDownLayout.onAppendLink(title, link, isLink)
    }

    interface CommentListener {
        fun onCreateComment(text: String, bundle: Bundle?) {}
        fun onSendActionClicked(text: String, bundle: Bundle?)
        fun onTagUser(username: String)
        fun onClearEditText()
        fun getNamesToTag(): ArrayList<String>?
    }

    companion object {
        fun newInstance(bundle: Bundle?): CommentEditorFragment {
            val fragment = CommentEditorFragment()
            bundle?.let {
                fragment.arguments = Bundler.start().put(BundleConstant.ITEM, bundle).end()
            }
            return fragment
        }
    }
}
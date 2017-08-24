package com.fastaccess.ui.modules.editor.comment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
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
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText

/**
 * Created by kosh on 21/08/2017.
 */
class CommentEditorFragment : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(), MarkDownLayout.MarkdownListener,
        EmojiMvp.EmojiCallback {

    @BindView(R.id.commentBox) lateinit var commentBox: View
    @BindView(R.id.markdDownLayout) lateinit var markdDownLayout: MarkDownLayout
    @BindView(R.id.commentText) lateinit var commentText: MarkdownEditText
    private var commentListener: CommentListener? = null

    @OnClick(R.id.sendComment) internal fun onComment() {
        if (!InputHelper.isEmpty(getEditText())) {
            commentListener?.onSendActionClicked(InputHelper.toString(getEditText()), arguments?.getBundle(BundleConstant.ITEM))
            getEditText().setText("")
            ViewHelper.hideKeyboard(getEditText())
        }
    }

    @OnClick(R.id.fullScreenComment) internal fun onExpandScreen() {
        val intent = Intent(context, EditorActivity::class.java)
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, BundleConstant.ExtraType.FOR_RESULT_EXTRA)
                .put(BundleConstant.EXTRA, getEditText().text.toString())
                .end())
        startActivityForResult(intent, BundleConstant.REQUEST_CODE)
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
        markdDownLayout.markdownListener = this
        if (savedInstanceState == null) {
            commentText.setText(arguments?.getBundle(BundleConstant.ITEM)?.getString(BundleConstant.EXTRA))
        }
    }

    override fun getEditText(): EditText = commentText

    override fun fragmentManager(): FragmentManager = childFragmentManager

    override fun getSavedText(): CharSequence? = commentText.savedText

    override fun onEmojiAdded(emoji: Emoji?) = markdDownLayout.onEmojiAdded(emoji)

    fun onAddUserName(username: String) {
        getEditText().setText(if (getEditText().text.isNullOrBlank()) {
            "@$username"
        } else {
            "${getEditText().text} @$username"
        })
        getEditText().setSelection(getEditText().text.length)
    }

    interface CommentListener {
        fun onSendActionClicked(text: String, bundle: Bundle?)
        fun onTagUser(username: String)
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
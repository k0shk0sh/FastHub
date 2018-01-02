package com.fastaccess.ui.modules.reviews

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener
import com.fastaccess.ui.widgets.SpannableBuilder

/**
 * Created by Kosh on 24 Jun 2017, 12:32 PM
 */
class AddReviewDialogFragment : BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.text) lateinit var textView: TextView
    @BindView(R.id.lineNo) lateinit var lineNo: TextView

    private val commentEditorFragment: CommentEditorFragment? by lazy {
        childFragmentManager.findFragmentByTag("CommentEditorFragment") as CommentEditorFragment?
    }
    private val spacePattern = "\\s+".toRegex()

    private var commentCallback: ReviewCommentListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        commentCallback = if (parentFragment is ReviewCommentListener) {
            parentFragment as ReviewCommentListener
        } else {
            context as ReviewCommentListener
        }
    }

    override fun onDetach() {
        commentCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.review_comment_dialog_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = CommentEditorFragment()
            fragment.arguments = Bundler.start().put(BundleConstant.YES_NO_EXTRA, true).end()
            childFragmentManager.beginTransaction()
                    .replace(R.id.commentFragmentContainer, fragment, "CommentEditorFragment")
                    .commitNow()
        }
        val item = arguments!!.getParcelable<CommitLinesModel>(BundleConstant.ITEM)
        lineNo.text = SpannableBuilder.builder()
                .append(if (item.leftLineNo >= 0) String.format("%s.", item.leftLineNo) else "")
                .append(if (item.rightLineNo >= 0) String.format("%s.", item.rightLineNo) else "")
        lineNo.visibility = if (InputHelper.isEmpty(lineNo)) View.GONE else View.VISIBLE

        val context = context ?: return
        when (item.color) {
            CommitLinesModel.ADDITION -> textView.setBackgroundColor(ViewHelper.getPatchAdditionColor(context))
            CommitLinesModel.DELETION -> textView.setBackgroundColor(ViewHelper.getPatchDeletionColor(context))
            CommitLinesModel.PATCH -> textView.setBackgroundColor(ViewHelper.getPatchRefColor(context))
            else -> textView.setBackgroundColor(Color.TRANSPARENT)
        }
        if (item.noNewLine) {
            textView.text = SpannableBuilder.builder().append(item.text.replace(spacePattern, " ")).append(" ")
                    .append(ContextCompat.getDrawable(context, R.drawable.ic_newline))
        } else {
            textView.text = item.text.replace(spacePattern, " ")
        }
        toolbar.setTitle(R.string.add_comment)
        toolbar.setNavigationIcon(R.drawable.ic_clear)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.inflateMenu(R.menu.add_menu)
        toolbar.setOnMenuItemClickListener {
            if (commentEditorFragment?.getEditText()?.text.isNullOrEmpty()) {
                commentEditorFragment?.getEditText()?.error = getString(R.string.required_field)
            } else {
                commentEditorFragment?.getEditText()?.error = null
                commentCallback?.onCommentAdded(InputHelper.toString(commentEditorFragment?.getEditText()?.text),
                        item, arguments!!.getBundle(BundleConstant.EXTRA))
                dismiss()
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    companion object {
        fun newInstance(commitLinesModel: CommitLinesModel, bundle: Bundle? = null): AddReviewDialogFragment {
            val dialog = AddReviewDialogFragment()
            dialog.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, commitLinesModel)
                    .put(BundleConstant.EXTRA, bundle)
                    .end()
            return dialog
        }
    }
}
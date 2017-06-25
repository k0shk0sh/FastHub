package com.fastaccess.ui.modules.reviews

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener
import com.fastaccess.ui.widgets.SpannableBuilder

/**
 * Created by Kosh on 24 Jun 2017, 12:32 PM
 */
class AddReviewDialogFragment : BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    val toolbar: Toolbar by lazy { view!!.findViewById(R.id.toolbar) as Toolbar }
    val textView: TextView by lazy { view!!.findViewById(R.id.text) as TextView }
    val lineNo: TextView by lazy { view!!.findViewById(R.id.lineNo) as TextView }
    val editText: TextInputLayout by lazy { view!!.findViewById(R.id.editText) as TextInputLayout }
    val spacePattern = "\\s+".toRegex()


    private var commentCallback: ReviewCommentListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (parentFragment is ReviewCommentListener) {
            commentCallback = parentFragment as ReviewCommentListener
        } else {
            commentCallback = context as ReviewCommentListener
        }
    }

    override fun onDetach() {
        commentCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.review_comment_dialog_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val item = arguments.getParcelable<CommitLinesModel>(BundleConstant.ITEM)
        lineNo.text = SpannableBuilder.builder()
                .append(if (item.leftLineNo >= 0) String.format("%s.", item.leftLineNo) else "")
                .append(if (item.rightLineNo >= 0) String.format("%s.", item.rightLineNo) else "")
        lineNo.visibility = if (InputHelper.isEmpty(lineNo)) View.GONE else View.VISIBLE

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
            if (editText.editText?.text.isNullOrEmpty()) {
                editText.error = getString(R.string.required_field)
            } else {
                editText.error = null
                commentCallback?.onCommentAdded(InputHelper.toString(editText), item, arguments.getBundle(BundleConstant.EXTRA))
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
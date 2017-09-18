package com.fastaccess.ui.modules.editor

import com.fastaccess.data.dao.EditReviewCommentModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.helper.BundleConstant
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.modules.editor.emoji.EmojiMvp
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp
import com.fastaccess.ui.widgets.markdown.MarkDownLayout

/**
 * Created by Kosh on 27 Nov 2016, 1:31 AM
 */

interface EditorMvp {

    interface View : BaseMvp.FAView, EditorLinkImageMvp.EditorLinkCallback,
            MarkDownLayout.MarkdownListener, EmojiMvp.EmojiCallback {
        fun onSendResultAndFinish(commentModel: Comment, isNew: Boolean)

        fun onSendMarkDownResult()

        fun onSendReviewResultAndFinish(comment: EditReviewCommentModel, isNew: Boolean)
    }

    interface Presenter : BaseMvp.FAPresenter {

        fun onEditGistComment(id: Long, savedText: CharSequence?, gistId: String)

        fun onSubmitGistComment(savedText: CharSequence?, gistId: String)

        fun onSubmitIssueComment(savedText: CharSequence, itemId: String, login: String, issueNumber: Int)

        fun onEditIssueComment(savedText: CharSequence, itemId: String, id: Long, login: String, issueNumber: Int)

        fun onSubmitCommitComment(savedText: CharSequence, itemId: String, login: String, sha: String)

        fun onEditCommitComment(savedText: CharSequence, itemId: String, login: String, id: Long)

        fun onHandleSubmission(savedText: CharSequence?, @BundleConstant.ExtraType extraType: String?,
                               itemId: String?, id: Long, login: String?, issueNumber: Int, sha: String?,
                               reviewComment: EditReviewCommentModel?)
    }
}

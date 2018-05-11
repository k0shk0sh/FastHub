package com.fastaccess.ui.modules.editor

import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.EditReviewCommentModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.BundleConstant.ExtraType.*
import com.fastaccess.helper.InputHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 27 Nov 2016, 1:31 AM
 */

class EditorPresenter : BasePresenter<EditorMvp.View>(), EditorMvp.Presenter {

    override fun onEditGistComment(id: Long, savedText: CharSequence?, gistId: String) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText!!.toString()
            makeRestCall<Comment>(RestProvider.getGistService(isEnterprise).editGistComment(gistId, id, requestModel),
                    { comment -> sendToView { view -> view.onSendResultAndFinish(comment, false) } }, false)
        }
    }

    override fun onSubmitGistComment(savedText: CharSequence?, gistId: String) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText!!.toString()
            makeRestCall<Comment>(RestProvider.getGistService(isEnterprise).createGistComment(gistId, requestModel),
                    { comment -> sendToView { view -> view.onSendResultAndFinish(comment, true) } }, false)
        }
    }

    override fun onHandleSubmission(savedText: CharSequence?, @BundleConstant.ExtraType extraType: String?,
                                    itemId: String?, id: Long, login: String?, issueNumber: Int,
                                    sha: String?, reviewComment: EditReviewCommentModel?) {
        if (extraType == null) {
            throw NullPointerException("extraType  is null")
        }
        when (extraType) {
            EDIT_GIST_COMMENT_EXTRA -> {
                if (itemId == null) {
                    throw NullPointerException("itemId is null")
                }
                onEditGistComment(id, savedText, itemId)
            }
            NEW_GIST_COMMENT_EXTRA -> {
                if (itemId == null) {
                    throw NullPointerException("itemId is null")
                }
                onSubmitGistComment(savedText, itemId)
            }
            FOR_RESULT_EXTRA -> sendToView({ it.onSendMarkDownResult() })
            EDIT_ISSUE_COMMENT_EXTRA -> {
                if (itemId == null || login == null) {
                    throw NullPointerException("itemId or login is null")
                }
                onEditIssueComment(savedText!!, itemId, id, login, issueNumber)
            }
            NEW_ISSUE_COMMENT_EXTRA -> {
                if (itemId == null || login == null) {
                    throw NullPointerException("itemId or login is null")
                }
                onSubmitIssueComment(savedText!!, itemId, login, issueNumber)
            }
            NEW_COMMIT_COMMENT_EXTRA -> {
                if (itemId == null || login == null || sha == null) {
                    throw NullPointerException("itemId or login is null")
                }
                onSubmitCommitComment(savedText!!, itemId, login, sha)
            }
            EDIT_COMMIT_COMMENT_EXTRA -> {
                if (itemId == null || login == null) {
                    throw NullPointerException("itemId or login is null")
                }
                onEditCommitComment(savedText!!, itemId, login, id)
            }
            NEW_REVIEW_COMMENT_EXTRA -> {
                if (reviewComment == null || itemId == null || login == null || savedText == null) {
                    throw NullPointerException("reviewComment null")
                }
                onSubmitReviewComment(reviewComment, savedText, itemId, login, issueNumber)
            }
            EDIT_REVIEW_COMMENT_EXTRA -> {
                if (reviewComment == null || itemId == null || login == null || savedText == null) {
                    throw NullPointerException("reviewComment null")
                }
                onEditReviewComment(reviewComment, savedText, itemId, login, issueNumber, id)
            }
        }
    }

    private fun onEditReviewComment(reviewComment: EditReviewCommentModel, savedText: CharSequence, repoId: String,
                                    login: String, @Suppress("UNUSED_PARAMETER") issueNumber: Int, id: Long) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText.toString()
            makeRestCall(RestProvider.getReviewService(isEnterprise).editComment(login, repoId, id, requestModel)
                    .map { comment ->
                        reviewComment.commentModel = comment
                        reviewComment
                    }, { comment -> sendToView { view -> view.onSendReviewResultAndFinish(comment, false) } }, false)
        }
    }

    private fun onSubmitReviewComment(reviewComment: EditReviewCommentModel, savedText: CharSequence,
                                      repoId: String, login: String, issueNumber: Int) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText.toString()
            requestModel.inReplyTo = reviewComment.inReplyTo
            makeRestCall(RestProvider.getReviewService(isEnterprise).submitComment(login, repoId, issueNumber.toLong(), requestModel)
                    .map { comment ->
                        reviewComment.commentModel = comment
                        reviewComment
                    }, { comment -> sendToView { view -> view.onSendReviewResultAndFinish(comment, true) } }, false)
        }
    }

    override fun onSubmitIssueComment(savedText: CharSequence, itemId: String, login: String, issueNumber: Int) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText.toString()
            makeRestCall<Comment>(RestProvider.getIssueService(isEnterprise).createIssueComment(login, itemId, issueNumber, requestModel)
            ) { comment -> sendToView { view -> view.onSendResultAndFinish(comment, true) } }
        }
    }

    override fun onEditIssueComment(savedText: CharSequence, itemId: String, id: Long, login: String, issueNumber: Int) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText.toString()
            makeRestCall<Comment>(RestProvider.getIssueService(isEnterprise).editIssueComment(login, itemId, id, requestModel),
                    { comment -> sendToView { view -> view.onSendResultAndFinish(comment, false) } }, false)
        }
    }

    override fun onSubmitCommitComment(savedText: CharSequence, itemId: String, login: String, sha: String) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText.toString()
            makeRestCall<Comment>(RestProvider.getRepoService(isEnterprise).postCommitComment(login, itemId, sha, requestModel),
                    { comment -> sendToView { view -> view.onSendResultAndFinish(comment, true) } }, false)
        }
    }

    override fun onEditCommitComment(savedText: CharSequence, itemId: String, login: String, id: Long) {
        if (!InputHelper.isEmpty(savedText)) {
            val requestModel = CommentRequestModel()
            requestModel.body = savedText.toString()
            makeRestCall<Comment>(RestProvider.getRepoService(isEnterprise).editCommitComment(login, itemId, id, requestModel),
                    { comment -> sendToView { view -> view.onSendResultAndFinish(comment, false) } }, false)
        }
    }
}

package com.fastaccess.ui.modules.editor;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import static com.fastaccess.helper.BundleConstant.ExtraTYpe.EDIT_COMMIT_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.EDIT_GIST_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.EDIT_ISSUE_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.EDIT_REVIEW_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.FOR_RESULT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.NEW_COMMIT_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.NEW_GIST_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.NEW_ISSUE_COMMENT_EXTRA;
import static com.fastaccess.helper.BundleConstant.ExtraTYpe.NEW_REVIEW_COMMENT_EXTRA;

/**
 * Created by Kosh on 27 Nov 2016, 1:31 AM
 */

class EditorPresenter extends BasePresenter<EditorMvp.View> implements EditorMvp.Presenter {

    @Override public void onActionClicked(@NonNull EditText editText, @IdRes int id) {
        if (editText.getSelectionEnd() == -1 || editText.getSelectionStart() == -1) {
            return;
        }
        switch (id) {
            case R.id.headerOne:
                MarkDownProvider.addHeader(editText, 1);
                break;
            case R.id.headerTwo:
                MarkDownProvider.addHeader(editText, 2);
                break;
            case R.id.headerThree:
                MarkDownProvider.addHeader(editText, 3);
                break;
            case R.id.bold:
                MarkDownProvider.addBold(editText);
                break;
            case R.id.italic:
                MarkDownProvider.addItalic(editText);
                break;
            case R.id.strikethrough:
                MarkDownProvider.addStrikeThrough(editText);
                break;
            case R.id.numbered:
                MarkDownProvider.addList(editText, "1.");
                break;
            case R.id.bullet:
                MarkDownProvider.addList(editText, "-");
                break;
            case R.id.header:
                MarkDownProvider.addDivider(editText);
                break;
            case R.id.code:
                MarkDownProvider.addCode(editText);
                break;
            case R.id.quote:
                MarkDownProvider.addQuote(editText);
                break;
            case R.id.link:
                MarkDownProvider.addLink(editText);
                break;
            case R.id.image:
                MarkDownProvider.addPhoto(editText);
                break;
            case R.id.checkbox:
                MarkDownProvider.addList(editText, "- [x]");
                break;
            case R.id.unCheckbox:
                MarkDownProvider.addList(editText, "- [ ]");
                break;
            case R.id.inlineCode:
                MarkDownProvider.addInlinleCode(editText);
                break;
        }
    }

    @Override public void onEditGistComment(long id, @Nullable CharSequence savedText, @NonNull String gistId) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            makeRestCall(RestProvider.getGistService(isEnterprise()).editGistComment(gistId, id, requestModel),
                    comment -> sendToView(view -> view.onSendResultAndFinish(comment, false)), false);
        }
    }

    @Override public void onSubmitGistComment(@Nullable CharSequence savedText, @NonNull String gistId) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            makeRestCall(RestProvider.getGistService(isEnterprise()).createGistComment(gistId, requestModel),
                    comment -> sendToView(view -> view.onSendResultAndFinish(comment, true)), false);
        }
    }

    @Override public void onHandleSubmission(@Nullable CharSequence savedText, @Nullable @BundleConstant.ExtraTYpe String extraType,
                                             @Nullable String itemId, long id, @Nullable String login, int issueNumber,
                                             @Nullable String sha, EditReviewCommentModel reviewComment) {
        if (extraType == null) {
            throw new NullPointerException("extraType  is null");
        }
        switch (extraType) {
            case EDIT_GIST_COMMENT_EXTRA:
                if (itemId == null) {
                    throw new NullPointerException("itemId is null");
                }
                onEditGistComment(id, savedText, itemId);
                break;
            case NEW_GIST_COMMENT_EXTRA:
                if (itemId == null) {
                    throw new NullPointerException("itemId is null");
                }
                onSubmitGistComment(savedText, itemId);
                break;
            case FOR_RESULT_EXTRA:
                sendToView(EditorMvp.View::onSendMarkDownResult);
                break;
            case EDIT_ISSUE_COMMENT_EXTRA:
                if (itemId == null || login == null) {
                    throw new NullPointerException("itemId or login is null");
                }
                onEditIssueComment(savedText, itemId, id, login, issueNumber);
                break;
            case NEW_ISSUE_COMMENT_EXTRA:
                if (itemId == null || login == null) {
                    throw new NullPointerException("itemId or login is null");
                }
                onSubmitIssueComment(savedText, itemId, login, issueNumber);
                break;
            case NEW_COMMIT_COMMENT_EXTRA:
                if (itemId == null || login == null || sha == null) {
                    throw new NullPointerException("itemId or login is null");
                }
                onSubmitCommitComment(savedText, itemId, login, sha);
                break;
            case EDIT_COMMIT_COMMENT_EXTRA:
                if (itemId == null || login == null) {
                    throw new NullPointerException("itemId or login is null");
                }
                onEditCommitComment(savedText, itemId, login, id);
                break;
            case NEW_REVIEW_COMMENT_EXTRA:
                if (reviewComment == null || itemId == null || login == null || savedText == null) {
                    throw new NullPointerException("reviewComment null");
                }
                onSubmitReviewComment(reviewComment, savedText, itemId, login, issueNumber);
                break;
            case EDIT_REVIEW_COMMENT_EXTRA:
                if (reviewComment == null || itemId == null || login == null || savedText == null) {
                    throw new NullPointerException("reviewComment null");
                }
                onEditReviewComment(reviewComment, savedText, itemId, login, issueNumber, id);
                break;
        }
    }

    private void onEditReviewComment(@NonNull EditReviewCommentModel reviewComment, @NonNull CharSequence savedText, @NonNull String repoId,
                                     @NonNull String login, int issueNumber, long id) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
//            requestModel.setInReplyTo(reviewComment.getInReplyTo());
            makeRestCall(RestProvider.getReviewService(isEnterprise()).editComment(login, repoId, id, requestModel)
                    .map(comment -> {
                        reviewComment.setCommentModel(comment);
                        return reviewComment;
                    }), comment -> sendToView(view -> view.onSendReviewResultAndFinish(comment, false)), false);
        }
    }

    private void onSubmitReviewComment(@NonNull EditReviewCommentModel reviewComment, @NonNull CharSequence savedText,
                                       @NonNull String repoId, @NonNull String login, int issueNumber) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            requestModel.setInReplyTo(reviewComment.getInReplyTo());
            makeRestCall(RestProvider.getReviewService(isEnterprise()).submitComment(login, repoId, issueNumber, requestModel)
                    .map(comment -> {
                        reviewComment.setCommentModel(comment);
                        return reviewComment;
                    }), comment -> sendToView(view -> view.onSendReviewResultAndFinish(comment, true)), false);
        }
    }

    @Override public void onSubmitIssueComment(CharSequence savedText, @NonNull String itemId, @NonNull String login, int issueNumber) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            makeRestCall(RestProvider.getIssueService(isEnterprise()).createIssueComment(login, itemId, issueNumber, requestModel),
                    comment -> sendToView(view -> view.onSendResultAndFinish(comment, true)));
        }
    }

    @Override public void onEditIssueComment(CharSequence savedText, @NonNull String itemId, long id, @NonNull String login, int issueNumber) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            makeRestCall(RestProvider.getIssueService(isEnterprise()).editIssueComment(login, itemId, id, requestModel),
                    comment -> sendToView(view -> view.onSendResultAndFinish(comment, false)), false);
        }
    }

    @Override public void onSubmitCommitComment(CharSequence savedText, @NonNull String itemId, @NonNull String login, @NonNull String sha) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            makeRestCall(RestProvider.getRepoService(isEnterprise()).postCommitComment(login, itemId, sha, requestModel),
                    comment -> sendToView(view -> view.onSendResultAndFinish(comment, true)), false);
        }
    }

    @Override public void onEditCommitComment(CharSequence savedText, @NonNull String itemId, @NonNull String login, long id) {
        if (!InputHelper.isEmpty(savedText)) {
            CommentRequestModel requestModel = new CommentRequestModel();
            requestModel.setBody(savedText.toString());
            makeRestCall(RestProvider.getRepoService(isEnterprise()).editCommitComment(login, itemId, id, requestModel),
                    comment -> sendToView(view -> view.onSendResultAndFinish(comment, false)), false);
        }
    }
}

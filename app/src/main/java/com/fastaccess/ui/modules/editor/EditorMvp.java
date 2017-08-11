package com.fastaccess.ui.modules.editor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.EditReviewCommentModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp;
import com.fastaccess.ui.widgets.MarkDownLayout;

/**
 * Created by Kosh on 27 Nov 2016, 1:31 AM
 */

interface EditorMvp {

    interface View extends BaseMvp.FAView, EditorLinkImageMvp.EditorLinkCallback, MarkDownLayout.MarkdownListener {
        void onSendResultAndFinish(@NonNull Comment commentModel, boolean isNew);

        void onSendMarkDownResult();

        void onSendReviewResultAndFinish(EditReviewCommentModel comment, boolean isNew);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onEditGistComment(long id, @Nullable CharSequence savedText, @NonNull String gistId);

        void onSubmitGistComment(@Nullable CharSequence savedText, @NonNull String gistId);

        void onSubmitIssueComment(CharSequence savedText, @NonNull String itemId, @NonNull String login, int issueNumber);

        void onEditIssueComment(CharSequence savedText, @NonNull String itemId, long id, @NonNull String login, int issueNumber);

        void onSubmitCommitComment(CharSequence savedText, @NonNull String itemId, @NonNull String login, @NonNull String sha);

        void onEditCommitComment(CharSequence savedText, @NonNull String itemId, @NonNull String login, long id);

        void onHandleSubmission(@Nullable CharSequence savedText, @Nullable @BundleConstant.ExtraTYpe String extraType,
                                @Nullable String itemId, long id, @Nullable String login, int issueNumber, @Nullable String sha,
                                EditReviewCommentModel reviewComment);
    }
}

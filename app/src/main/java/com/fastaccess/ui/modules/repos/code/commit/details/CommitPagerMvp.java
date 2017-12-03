package com.fastaccess.ui.modules.repos.code.commit.details;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;

/**
 * Created by Kosh on 10 Dec 2016, 9:21 AM
 */

public interface CommitPagerMvp {

    interface View extends BaseMvp.FAView, CommentEditorFragment.CommentListener {

        void onSetup();

        void onFinishActivity();

        void onAddComment(@NonNull Comment newComment);

        String getLogin();

        String getRepoId();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable Commit getCommit();

        void onActivityCreated(@Nullable Intent intent);

        void onWorkOffline(@NonNull String sha, @NonNull String repoId, @NonNull String login);

        String getLogin();

        String getRepoId();

        boolean showToRepoBtn();
    }

}

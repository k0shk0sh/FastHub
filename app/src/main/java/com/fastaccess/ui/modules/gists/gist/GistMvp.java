package com.fastaccess.ui.modules.gists.gist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.editor.comment.CommentEditorFragment;

/**
 * Created by Kosh on 12 Nov 2016, 12:17 PM
 */

interface GistMvp {

    interface View extends BaseMvp.FAView, CommentEditorFragment.CommentListener {
        void onSuccessDeleted();

        void onErrorDeleting();

        void onGistStarred(boolean isStarred);

        void onGistForked(boolean isForked);

        void onSetupDetails();

        void onUpdatePinIcon(Gist gist);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable Gist getGist();

        @NonNull String gistId();

        void onActivityCreated(@Nullable Intent intent);

        void onDeleteGist();

        boolean isOwner();

        void onStarGist();

        void onForkGist();

        boolean isForked();

        boolean isStarred();

        void checkStarring(@NonNull String gistId);

        void callApi();

        void onWorkOffline(@NonNull String gistId);

        void onPinUnpinGist();
    }
}

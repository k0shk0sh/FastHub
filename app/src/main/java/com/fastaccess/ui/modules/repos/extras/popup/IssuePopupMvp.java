package com.fastaccess.ui.modules.repos.extras.popup;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 27 May 2017, 1:55 PM
 */

public interface IssuePopupMvp {

    interface View extends BaseMvp.FAView {
        void onSuccessfullySubmitted();
    }

    interface Presenter {
        void onSubmit(@NonNull String login, @NonNull String repoId, int issueNumber, @NonNull String text);
    }
}

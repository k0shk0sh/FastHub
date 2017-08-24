package com.fastaccess.ui.modules.repos.extras.popup;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 27 May 2017, 1:56 PM
 */

public class IssuePopupPresenter extends BasePresenter<IssuePopupMvp.View> implements IssuePopupMvp.Presenter {

    @Override public void onSubmit(@NonNull String login, @NonNull String repoId, int issueNumber, @NonNull String text) {
        CommentRequestModel requestModel = new CommentRequestModel();
        requestModel.setBody(text);
        makeRestCall(RestProvider.getIssueService(isEnterprise()).createIssueComment(login, repoId, issueNumber, requestModel),
                comment -> sendToView(IssuePopupMvp.View::onSuccessfullySubmitted));
    }


}

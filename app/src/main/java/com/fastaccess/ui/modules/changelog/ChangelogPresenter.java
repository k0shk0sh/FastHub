package com.fastaccess.ui.modules.changelog;

import com.fastaccess.App;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.gson.ToGsonProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import lombok.Getter;

/**
 * Created by Kosh on 28 May 2017, 10:53 AM
 */

@Getter public class ChangelogPresenter extends BasePresenter<ChangelogMvp.View> implements ChangelogMvp.Presenter {
    private String html;

    @Override public void onLoadChangelog() {
        manageDisposable(RxHelper.getObservable(ToGsonProvider.getChangelog(App.getInstance()))
                .subscribe(s -> {
                    this.html = s;
                    sendToView(view -> view.onChangelogLoaded(html));
                }, throwable -> sendToView(view -> view.onChangelogLoaded(null))));
    }
}

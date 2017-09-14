package com.fastaccess.ui.modules.repos.code.prettifier;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.prettifier.pretty.PrettifyWebView;

/**
 * Created by Kosh on 27 Nov 2016, 3:41 PM
 */

interface ViewerMvp {

    interface View extends BaseMvp.FAView, PrettifyWebView.OnContentChangedListener {

        void onSetImageUrl(@NonNull String url, boolean isSvg);

        void onSetMdText(@NonNull String text, String baseUrl, boolean replace);

        void onSetCode(@NonNull String text);

        void onShowError(@NonNull String msg);

        void onShowError(@StringRes int msg);

        void onShowMdProgress();

        void openUrl(@NonNull String url);

        void onViewAsCode();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onHandleIntent(@Nullable Bundle intent);

        void onLoadContentAsStream();

        String downloadedStream();

        boolean isMarkDown();

        void onWorkOffline();

        void onWorkOnline();

        boolean isRepo();

        boolean isImage();

        @NonNull String url();
    }
}

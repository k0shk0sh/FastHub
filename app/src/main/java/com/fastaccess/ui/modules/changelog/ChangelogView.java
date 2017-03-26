package com.fastaccess.ui.modules.changelog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.changelog.ChangelogProvider;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontButton;
import com.fastaccess.ui.widgets.FontTextView;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;
import rx.Subscription;

/**
 * Created by Kosh on 26 Mar 2017, 10:15 PM
 */

public class ChangelogView extends BaseBottomSheetDialog {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.message) FontTextView message;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.messageLayout) View messageLayout;
    @BindView(R.id.prettifyWebView) PrettifyWebView prettifyWebView;
    @BindView(R.id.webProgress) ProgressBar webProgress;
    @State String html;

    private Subscription subscription;

    @OnClick(R.id.ok) void onOk() {
        dismiss();
    }

    @Override protected int layoutRes() {
        return R.layout.message_dialog;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageLayout.setBackgroundColor(Color.WHITE);
        if (savedInstanceState == null) {
            if (!BuildConfig.DEBUG) {
                PrefGetter.setWhatsNewVersion();
            }
        }
        webProgress.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        title.setText(R.string.changelog);
        if (html == null) {
            subscription = RxHelper.getObserver(ChangelogProvider.getChangelog(getContext()))
                    .subscribe(s -> {
                        this.html = s;
                        showChangelog();
                    });
        } else {
            showChangelog();
        }
    }

    private void showChangelog() {
        webProgress.setVisibility(View.GONE);
        if (html != null) {
            message.setVisibility(View.GONE);
            prettifyWebView.setVisibility(View.VISIBLE);
            prettifyWebView.setGithubContent(html, null, true);
            prettifyWebView.setNestedScrollingEnabled(false);
        }
    }

    @Override public void onDestroyView() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroyView();
    }
}

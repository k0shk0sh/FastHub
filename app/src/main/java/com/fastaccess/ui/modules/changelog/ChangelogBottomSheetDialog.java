package com.fastaccess.ui.modules.changelog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment;
import com.fastaccess.ui.widgets.FontButton;
import com.fastaccess.ui.widgets.FontTextView;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 26 Mar 2017, 10:15 PM
 */

public class ChangelogBottomSheetDialog extends BaseMvpBottomSheetDialogFragment<ChangelogMvp.View, ChangelogPresenter> implements
        ChangelogMvp.View {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.message) FontTextView message;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.messageLayout) View messageLayout;
    @BindView(R.id.prettifyWebView) PrettifyWebView prettifyWebView;
    @BindView(R.id.webProgress) ProgressBar webProgress;

    @OnClick(R.id.ok) void onOk() {
        dismiss();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            PrefGetter.setWhatsNewVersion();
        }
        webProgress.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.GONE);
        title.setText(R.string.changelog);
        if (getPresenter().getHtml() == null) {
            getPresenter().onLoadChangelog();
        } else {
            showChangelog(getPresenter().getHtml());
        }
    }

    @Override protected int fragmentLayout() {
        return R.layout.message_dialog;
    }

    @Override public void onChangelogLoaded(@Nullable String html) {
        showChangelog(html);
    }

    @NonNull @Override public ChangelogPresenter providePresenter() {
        return new ChangelogPresenter();
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void showChangelog(String html) {
        if (prettifyWebView == null) return;
        webProgress.setVisibility(View.GONE);
        if (html != null) {
            message.setVisibility(View.GONE);
            prettifyWebView.setVisibility(View.VISIBLE);
            prettifyWebView.setGithubContent(html, null, false, false);
            prettifyWebView.setNestedScrollingEnabled(false);
        }
    }
}

package com.fastaccess.ui.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontButton;
import com.fastaccess.ui.widgets.FontTextView;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 16 Sep 2016, 2:15 PM
 */

public class MessageDialogView extends BaseBottomSheetDialog {

    public static final String TAG = MessageDialogView.class.getSimpleName();

    public interface MessageDialogViewActionCallback {

        void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle);

        void onDialogDismissed();

    }

    @BindView(R.id.prettifyWebView) PrettifyWebView prettifyWebView;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.message) FontTextView message;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.ok) FontButton ok;

    @Nullable private MessageDialogViewActionCallback callback;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null && getParentFragment() instanceof MessageDialogViewActionCallback) {
            callback = (MessageDialogViewActionCallback) getParentFragment();
        } else if (context instanceof MessageDialogViewActionCallback) {
            callback = (MessageDialogViewActionCallback) context;
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onClick(@NonNull View view) {
        if (callback != null) {
            isAlreadyHidden = true;
            callback.onMessageDialogActionClicked(view.getId() == R.id.ok, getArguments().getBundle("bundle"));
        }
        dismiss();
    }

    @Override protected int layoutRes() {
        return R.layout.message_dialog;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        title.setText(bundle.getString("bundleTitle"));
        String msg = bundle.getString("bundleMsg");
        if (bundle.getBoolean("isMarkDown")) {
            if (msg != null) {
                message.setVisibility(View.GONE);
                prettifyWebView.setVisibility(View.VISIBLE);
                prettifyWebView.setGithubContent(msg, null, true);
                prettifyWebView.setNestedScrollingEnabled(false);
                return;
            }
        }
        if (bundle.getBundle("bundle") != null) {
            @SuppressWarnings("ConstantConditions")
            boolean yesNo = bundle.getBundle("bundle").getBoolean(BundleConstant.YES_NO_EXTRA);
            if (yesNo) {
                ok.setText(R.string.yes);
                cancel.setText(R.string.no);
            } else if (!bundle.getBundle("bundle").getString("primary_extra", "").isEmpty()){
                ok.setText(bundle.getBundle("bundle").getString("primary_extra", ""));
                cancel.setText(bundle.getBundle("bundle").getString("secondary_extra", ""));
            }
        }
        message.setText(msg);
    }

    @Override protected void onDismissedByScrolling() {
        super.onDismissedByScrolling();
        if (callback != null) callback.onDialogDismissed();
    }

    @Override protected void onHidden() {
        if (callback != null) callback.onDialogDismissed();
        super.onHidden();
    }

    @NonNull public static MessageDialogView newInstance(@NonNull String bundleTitle, @NonNull String bundleMsg) {
        return newInstance(bundleTitle, bundleMsg, null);
    }

    @NonNull public static MessageDialogView newInstance(@NonNull String bundleTitle, @NonNull String bundleMsg, boolean isMarkDown) {
        return newInstance(bundleTitle, bundleMsg, isMarkDown, null);
    }

    @NonNull public static MessageDialogView newInstance(@NonNull String bundleTitle, @NonNull String bundleMsg, boolean isMarkDown,
                                                         @Nullable Bundle bundle) {
        MessageDialogView messageDialogView = new MessageDialogView();
        messageDialogView.setArguments(getBundle(bundleTitle, bundleMsg, isMarkDown, bundle));
        return messageDialogView;
    }

    @NonNull public static MessageDialogView newInstance(@NonNull String bundleTitle, @NonNull String bundleMsg, @Nullable Bundle bundle) {
        return newInstance(bundleTitle, bundleMsg, false, bundle);
    }

    public static Bundle getBundle(String bundleTitle, String bundleMsg, boolean isMarkDown, Bundle bundle) {
        return Bundler
                .start()
                .put("bundleTitle", bundleTitle)
                .put("bundleMsg", bundleMsg)
                .put("bundle", bundle)
                .put("isMarkDown", isMarkDown)
                .end();
    }
}

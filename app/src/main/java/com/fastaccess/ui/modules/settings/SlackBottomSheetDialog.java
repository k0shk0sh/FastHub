package com.fastaccess.ui.modules.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontButton;
import com.fastaccess.ui.widgets.FontTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 01 May 2017, 12:58 AM
 */

public class SlackBottomSheetDialog extends BaseBottomSheetDialog {
    public interface SlackDialogListener {
        void onDismissed();
    }

    public static final String TAG = SlackBottomSheetDialog.class.getSimpleName();

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.message) FontTextView message;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.ok) FontButton ok;
    private SlackDialogListener listener;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SlackDialogListener) {
            listener = (SlackDialogListener) context;
        }
    }

    @Override public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override protected int layoutRes() {
        return R.layout.message_dialog;
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ok:
                ActivityHelper.startCustomTab(getActivity(), "http://rebrand.ly/fasthub");
                break;
        }
        if (listener != null) listener.onDismissed();
        dismiss();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancel.setText(R.string.no);
        ok.setText(R.string.yes);
        title.setText(R.string.join_slack);
        message.setText(getString(R.string.join_slack_message));
    }

    @Override protected void onHidden() {
        if (listener != null) listener.onDismissed();
        super.onHidden();
    }

    @Override protected void onDismissedByScrolling() {
        if (listener != null) listener.onDismissed();
        super.onDismissedByScrolling();
    }
}

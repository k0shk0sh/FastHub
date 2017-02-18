package com.fastaccess.ui.widgets.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import com.fastaccess.helper.Bundler;

/**
 * Created by Kosh on 09 Dec 2016, 5:18 PM
 */

public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG = ProgressDialogFragment.class.getSimpleName();

    public static ProgressDialogFragment newInstance(@NonNull Resources resources, @StringRes int msgId, boolean isCancelable) {
        return newInstance(resources.getString(msgId), isCancelable);
    }

    public static ProgressDialogFragment newInstance(@NonNull String msg, boolean isCancelable) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(Bundler.start()
                .put("msg", msg)
                .put("isCancelable", isCancelable)
                .end());
        return fragment;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getArguments().getString("msg"));
        progressDialog.setCancelable(getArguments().getBoolean("isCancelable"));
        setCancelable(getArguments().getBoolean("isCancelable"));
        return progressDialog;
    }
}

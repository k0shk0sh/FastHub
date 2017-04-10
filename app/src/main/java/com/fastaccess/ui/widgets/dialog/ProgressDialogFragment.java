package com.fastaccess.ui.widgets.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.Bundler;

/**
 * Created by Kosh on 09 Dec 2016, 5:18 PM
 */

public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG = ProgressDialogFragment.class.getSimpleName();

    @NonNull public static ProgressDialogFragment newInstance(@NonNull Resources resources, @StringRes int msgId, boolean isCancelable) {
        return newInstance(resources.getString(msgId), isCancelable);
    }

    @NonNull public static ProgressDialogFragment newInstance(@NonNull String msg, boolean isCancelable) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(Bundler.start()
                .put("msg", msg)
                .put("isCancelable", isCancelable)
                .end());
        return fragment;
    }

    @Override public void dismiss() {
        AnimHelper.dismissDialog(this, getResources().getInteger(android.R.integer.config_shortAnimTime), new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                try {
                    ProgressDialogFragment.super.dismiss();//FIXME PLEASEEEEEEEEEEEEE
                } catch (Exception e) {
                    if (getDialog() != null && getDialog().isShowing()) {
                        getDialog().dismiss();
                    }
                }
            }
        });
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getArguments().getString("msg"));
        progressDialog.setCancelable(getArguments().getBoolean("isCancelable"));
        setCancelable(getArguments().getBoolean("isCancelable"));
        progressDialog.setOnShowListener(dialogInterface -> AnimHelper.revealDialog(progressDialog,
                getResources().getInteger(android.R.integer.config_shortAnimTime)));
        return progressDialog;
    }
}

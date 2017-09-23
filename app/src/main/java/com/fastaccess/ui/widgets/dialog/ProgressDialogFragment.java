package com.fastaccess.ui.widgets.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import net.grandcentrix.thirtyinch.TiPresenter;

/**
 * Created by Kosh on 09 Dec 2016, 5:18 PM
 */

public class ProgressDialogFragment extends BaseDialogFragment {

    public ProgressDialogFragment() {
        suppressAnimation = true;
    }

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

    @Override protected int fragmentLayout() {
        return R.layout.progress_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        setCancelable(false);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }
}

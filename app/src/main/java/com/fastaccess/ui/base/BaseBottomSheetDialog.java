package com.fastaccess.ui.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.helper.ViewHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

/**
 * Created by Kosh on 16 Sep 2016, 2:11 PM
 */

@SuppressWarnings("RestrictedApi") public abstract class BaseBottomSheetDialog extends BottomSheetDialogFragment {

    protected BottomSheetBehavior<View> bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                isAlreadyHidden = true;
                onHidden();
            }
        }

        @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (slideOffset == -1.0) {
                isAlreadyHidden = true;
                onDismissedByScrolling();
            }
        }
    };
    protected boolean isAlreadyHidden;
    @Nullable private Unbinder unbinder;

    @LayoutRes protected abstract int layoutRes();

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Override public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), layoutRes(), null);
        dialog.setContentView(contentView);
        View parent = ((View) contentView.getParent());
        bottomSheetBehavior = BottomSheetBehavior.from(parent);
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        unbinder = ButterKnife.bind(this, contentView);
        onViewCreated(contentView, dialog.onSaveInstanceState());
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            if (ViewHelper.isTablet(getContext())) {
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
            }
            onDialogIsShowing();
        });
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                isAlreadyHidden = true;
                onDismissedByScrolling();
            }
            return false;
        });
        return dialog;
    }

    @Override public void onDetach() {
        if (!isAlreadyHidden) {
            onDismissedByScrolling();
        }
        super.onDetach();
    }

    protected void onHidden() {
        dismiss();
    }//helper method to notify dialogs

    protected void onDismissedByScrolling() {}//helper method to notify dialogs

    protected void onDialogIsShowing() {}//helper method to notify dialogs

}
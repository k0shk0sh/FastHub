package com.fastaccess.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.evernote.android.state.StateSaver;
import com.fastaccess.R;
import com.fastaccess.helper.ViewHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kosh on 16 Sep 2016, 2:11 PM
 */

@SuppressWarnings("RestrictedApi") public abstract class BaseBottomSheetDialog extends BottomSheetDialogFragment {

    protected BottomSheetBehavior<View> bottomSheetBehavior;
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
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
        StateSaver.saveInstanceState(this, outState);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            StateSaver.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getContext(), getContext().getTheme());
        LayoutInflater themeAwareInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = themeAwareInflater.inflate(layoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                View parent = getDialog().findViewById(R.id.design_bottom_sheet);
                if (parent != null) {
                    bottomSheetBehavior = BottomSheetBehavior.from(parent);
                    if (bottomSheetBehavior != null) {
                        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }
        });
        return view;
    }

    @Override public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            if (ViewHelper.isTablet(getActivity())) {
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
        try {
            dismiss();
        } catch (IllegalStateException ignored) {} //FML FIXME
    }

    protected void onDismissedByScrolling() {}

    private void onDialogIsShowing() {}

}
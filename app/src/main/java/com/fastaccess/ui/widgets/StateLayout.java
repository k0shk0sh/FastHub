package com.fastaccess.ui.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.fastaccess.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kosh on 20 Nov 2016, 12:21 AM
 */
public class StateLayout extends NestedScrollView {

    private static final int SHOW_PROGRESS_STATE = 1;
    private static final int HIDE_PROGRESS_STATE = 2;
    private static final int HIDE_RELOAD_STATE = 3;
    private static final int SHOW_RELOAD_STATE = 4;
    private static final int SHOW_EMPTY_STATE = 7;
    private static final int HIDDEN = 5;
    private static final int SHOWN = 6;
    private OnClickListener onReloadListener;

    @BindView(R.id.empty_text) FontTextView emptyText;
    @BindView(R.id.reload) FontButton reload;

    @State int layoutState = HIDDEN;
    @State String emptyTextValue;
    @State boolean showReload = true;

    @OnClick(R.id.reload) void onReload() {
        if (onReloadListener != null) {
            onReloadListener.onClick(reload);
        }
    }

    public StateLayout(Context context) {
        super(context);
    }

    public StateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showProgress() {
        layoutState = SHOW_PROGRESS_STATE;
        setVisibility(VISIBLE);
        emptyText.setVisibility(GONE);
        reload.setVisibility(GONE);
    }

    public void hideProgress() {
        layoutState = HIDE_PROGRESS_STATE;
        emptyText.setVisibility(VISIBLE);
        reload.setVisibility(VISIBLE);
        setVisibility(GONE);
    }

    public void hideReload() {
        layoutState = HIDE_RELOAD_STATE;
        reload.setVisibility(GONE);
        emptyText.setVisibility(GONE);
        setVisibility(GONE);
    }

    public void showReload(int adapterCount) {
        showReload = adapterCount == 0;
        showReload();
    }

    protected void showReload() {
        hideProgress();
        if (showReload) {
            layoutState = SHOW_RELOAD_STATE;
            reload.setVisibility(VISIBLE);
            emptyText.setVisibility(VISIBLE);
            setVisibility(VISIBLE);
        }
    }

    public void setEmptyText(@StringRes int resId) {
        setEmptyText(getResources().getString(resId));
    }

    public void setEmptyText(@NonNull String text) {
        this.emptyTextValue = text + "\n\n¯\\_(ツ)_/¯";
        emptyText.setText(emptyTextValue);
    }

    public void showEmptyState() {
        hideProgress();
        hideReload();
        setVisibility(VISIBLE);
        emptyText.setVisibility(VISIBLE);
        layoutState = SHOW_EMPTY_STATE;// last so it override visibility state.
    }

    public void setOnReloadListener(OnClickListener onReloadListener) {
        this.onReloadListener = onReloadListener;
    }

    @Override public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            layoutState = HIDDEN;
        } else {
            layoutState = SHOWN;
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.empty_layout, this);
        if (isInEditMode()) return;
        ButterKnife.bind(this);
        emptyText.setFreezesText(true);
    }

    @Override protected void onDetachedFromWindow() {
        onReloadListener = null;
        super.onDetachedFromWindow();
    }

    @Override public Parcelable onSaveInstanceState() {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state));
        onHandleLayoutState();
    }

    private void onHandleLayoutState() {
        setEmptyText(emptyTextValue);
        switch (layoutState) {
            case SHOW_PROGRESS_STATE:
                showProgress();
                break;
            case HIDE_PROGRESS_STATE:
                hideProgress();
                break;
            case HIDE_RELOAD_STATE:
                hideReload();
                break;
            case SHOW_RELOAD_STATE:
                showReload();
                break;
            case HIDDEN:
                setVisibility(GONE);
                break;
            case SHOW_EMPTY_STATE:
                showEmptyState();
                break;
            case SHOWN:
                setVisibility(VISIBLE);
                showReload();
                break;
        }
    }
}

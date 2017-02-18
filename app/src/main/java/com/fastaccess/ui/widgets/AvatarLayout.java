package com.fastaccess.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.modules.user.UserPagerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Kosh on 14 Nov 2016, 7:59 PM
 */

public class AvatarLayout extends FrameLayout implements ImageLoadingListener {

    @BindView(R.id.avatar) CircleImageView avatar;
    @BindView(R.id.avatarProgress) ProgressBar avatarProgress;
    private String login;
    private Toast toast;

    @OnClick(R.id.avatar) void onClick(View view) {
        if (InputHelper.isEmpty(login)) return;
        UserPagerView.startActivity(view.getContext(), login);
    }

    @OnLongClick(R.id.avatar) boolean onLongClick(View view) {
        if (InputHelper.isEmpty(login)) return false;
        if (toast != null) toast.cancel();
        toast = Toast.makeText(getContext(), view.getContentDescription(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return true;
    }

    public AvatarLayout(@NonNull Context context) {
        super(context);
    }

    public AvatarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AvatarLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.avatar_layout, this);
        if (isInEditMode()) return;
        ButterKnife.bind(this);
    }

    @Override public void onLoadingStarted(String imageUri, View view) {
        avatarProgress.setVisibility(VISIBLE);
    }

    @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        avatarProgress.setVisibility(GONE);
        avatar.setImageResource(R.drawable.ic_github_black);
    }

    @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        avatarProgress.setVisibility(GONE);
    }

    @Override public void onLoadingCancelled(String imageUri, View view) {}

    public void setUrl(@Nullable String url, @Nullable String login) {
        this.login = login;
        avatar.setContentDescription(login);
        if (url != null) {
            ImageLoader.getInstance().displayImage(url, avatar, this);
        } else {
            ImageLoader.getInstance().displayImage(null, avatar);
            avatarProgress.setVisibility(GONE);
        }
    }
}

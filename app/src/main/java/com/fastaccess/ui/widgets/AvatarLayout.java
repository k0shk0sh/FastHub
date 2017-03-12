package com.fastaccess.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.ui.modules.user.UserPagerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;

/**
 * Created by Kosh on 14 Nov 2016, 7:59 PM
 */

public class AvatarLayout extends FrameLayout implements ImageLoadingListener {

    @BindView(R.id.avatar) ShapedImageView avatar;
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
        if (PrefGetter.isRectdAvatar()) {
            avatar.setShape(ShapedImageView.SHAPE_MODE_ROUND_RECT, 15);
        }
    }

    @Override public void onLoadingStarted(String imageUri, View view) {
        setBackground(false);
    }

    @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        setBackground(true);
    }

    @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        setBackground(true);
    }

    @Override public void onLoadingCancelled(String imageUri, View view) {}

    public void setUrl(@Nullable String url, @Nullable String login) {
        this.login = login;
        avatar.setContentDescription(login);
        if (url != null) {
            ImageLoader.getInstance().displayImage(url, avatar, this);
        } else {
            ImageLoader.getInstance().displayImage(null, avatar);
        }
    }

    private void setBackground(boolean clear) {
        if (clear) {
            setBackgroundColor(Color.TRANSPARENT);
        } else {
            if (PrefGetter.isRectdAvatar()) {
                setBackgroundResource(R.drawable.rect_shape);
            } else {
                setBackgroundResource(R.drawable.circle_shape);
            }
        }
    }
}

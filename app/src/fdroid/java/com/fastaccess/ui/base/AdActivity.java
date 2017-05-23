package com.fastaccess.ui.base;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.fastaccess.R;

import net.grandcentrix.thirtyinch.TiActivity;
import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;

import butterknife.BindView;

/**
 * Created by thermatk on 12.04.17.
 */

public abstract class AdActivity<V extends TiView,P extends TiPresenter<V>> extends TiActivity<P, V> {

    @Nullable @BindView(R.id.adView) TextView adView;

    protected void showHideAds() {

    }
}

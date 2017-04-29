package com.fastaccess.ui.base;

import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.PrefGetter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.grandcentrix.thirtyinch.TiActivity;
import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;

import butterknife.BindView;

/**
 * Created by thermatk on 12.04.17.
 */

public abstract class AdActivity<V extends TiView, P extends TiPresenter<V>> extends TiActivity<P, V> {

    @Nullable @BindView(R.id.adView) AdView adView;

    @Override protected void onResume() {
        super.onResume();
        if (adView != null && adView.isShown()) {
            adView.resume();
        }
    }

    @Override protected void onPause() {
        if (adView != null && adView.isShown()) {
            adView.pause();
        }
        super.onPause();
    }

    @Override protected void onDestroy() {
        if (adView != null && adView.isShown()) {
            adView.destroy();
        }
        super.onDestroy();
    }

    protected void showHideAds() {
        if (adView != null) {
            boolean isAdsEnabled = PrefGetter.isAdsEnabled();
            if (isAdsEnabled) {
                adView.setVisibility(View.VISIBLE);
                MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(getString(R.string.test_device_id))
                        .build();
                adView.loadAd(adRequest);
            } else {
                adView.destroy();
                adView.setVisibility(View.GONE);
            }
        }
    }
}

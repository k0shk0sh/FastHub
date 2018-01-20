package com.fastaccess.ui.modules.main.donation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.view.View;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.miguelbcr.io.rx_billing_service.RxBillingService;
import com.miguelbcr.io.rx_billing_service.entities.ProductType;
import com.miguelbcr.io.rx_billing_service.entities.Purchase;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 24 Mar 2017, 9:16 PM
 */

public class DonationActivity extends BaseActivity {
    @BindView(R.id.cardsHolder) View cardsHolder;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    @Override protected int layout() {
        return R.layout.support_development_layout;
    }

    @Override protected boolean isTransparent() {
        return false;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return true;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimHelper.animateVisibility(cardsHolder, true);
        checkPurchase();
    }

    @OnClick(R.id.two) void onTwoClicked(View v) {
        onProceed(getString(R.string.donation_product_1));
    }

    @OnClick(R.id.five) void onFiveClicked(View v) {
        onProceed(getString(R.string.donation_product_2));
    }

    @OnClick(R.id.ten) void onTenClicked(View v) {
        onProceed(getString(R.string.donation_product_3));
    }

    @OnClick(R.id.twenty) void onTwentyClicked(View v) {
        onProceed(getString(R.string.donation_product_4));
    }

    @OnClick(R.id.premium) void onNavToPremium() {
        PremiumActivity.Companion.startActivity(this);
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    private void onProceed(@NonNull String productKey) {
        if (AppHelper.isGoogleAvailable(this)) {
            DonateActivity.Companion.start(this, productKey, null, null);
        } else {
            showErrorMessage(getString(R.string.google_play_service_error));
        }
    }

    private void checkPurchase() {
        ((BasePresenter) getPresenter()).manageViewDisposable(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .getPurchases(ProductType.IN_APP)
                .subscribe((purchases, throwable) -> {
                    if (purchases != null && !purchases.isEmpty()) {
                        for (Purchase purchase : purchases) {
                            String sku = purchase.sku();
                            if (!InputHelper.isEmpty(sku)) {
                                DonateActivity.Companion.enableProduct(sku, App.getInstance());
                            }
                        }
                    }
                }));
    }
}

package com.fastaccess.ui.modules.main.donation;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.PurchaseEvent;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.miguelbcr.io.rx_billing_service.RxBillingService;
import com.miguelbcr.io.rx_billing_service.RxBillingServiceException;
import com.miguelbcr.io.rx_billing_service.entities.ProductType;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by Kosh on 24 Mar 2017, 9:16 PM
 */

public class DonationActivity extends BaseActivity {
    @BindView(R.id.cardsHolder) View cardsHolder;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    private Disposable subscription;

    @Override protected void onDestroy() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
        super.onDestroy();
    }

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
        return false;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnimHelper.animateVisibility(cardsHolder, true);
    }

    @Override protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.two) public void onTwoClicked() {
        onProceed(getString(R.string.donation_product_1));
    }

    @OnClick(R.id.five) public void onFiveClicked() {
        onProceed(getString(R.string.donation_product_2));
    }

    @OnClick(R.id.ten) public void onTenClicked() {
        onProceed(getString(R.string.donation_product_3));
    }

    @OnClick(R.id.twenty) public void onTwentyClicked() {
        onProceed(getString(R.string.donation_product_4));
    }

    @NonNull @Override public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    private void onProceed(@NonNull String productKey) {
//        RxBillingService.getInstance(this, BuildConfig.DEBUG)
//                .getPurchases(ProductType.IN_APP)
//                .subscribe((purchases, throwable) -> Logger.e(purchases));
        subscription = RxHelper.getSingle(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .purchase(ProductType.IN_APP, productKey, "inapp:com.fastaccess.github:" + productKey))
                .doOnSubscribe(disposable -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT))
                .doFinally(() -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER))
                .subscribe((purchase, throwable) -> {
                    if (throwable == null) {
                        Answers.getInstance().logPurchase(new PurchaseEvent().putItemName(productKey));
                        showMessage(R.string.success, R.string.success_purchase_message);
                    } else {
                        if (throwable instanceof RxBillingServiceException) {
                            Logger.e(((RxBillingServiceException) throwable).getCode());
                        }
                        throwable.printStackTrace();
                    }
                });
    }
}

package com.fastaccess.ui.modules.main.donation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

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

//    private Pago pago;
    private Disposable subscription;

//    @NonNull public Pago getPago() {
//        if (pago == null) {
//            pago = new Pago(getApplicationContext());
//        }
//        return pago;
//    }

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
//        subscription = getPago().purchaseProduct(productKey, "inapp:com.fastaccess.github:" + productKey)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .onErrorReturn(throwable -> {
//                    showErrorMessage(throwable.getMessage());
//                    return null;
//                })
//                .subscribe(order -> {
//                    Logger.e(order);
//                    if (order != null) showMessage(R.string.success, R.string.success_purchase_message);
//                }, Throwable::printStackTrace);
    }
}

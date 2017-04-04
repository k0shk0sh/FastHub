package com.fastaccess.ui.modules.main.donation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.adapter.SimpleListAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.Arrays;

import butterknife.BindView;
import io.octo.bear.pago.Pago;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Kosh on 24 Mar 2017, 9:16 PM
 */

public class DonationView extends BaseDialogFragment<DonationMvp.View, DonationPresenter> implements DonationMvp.View {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    private Pago pago;
    private Subscription subscription;

    @Override protected int fragmentLayout() {
        return R.layout.simple_list_dialog;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title.setText(R.string.support_development);

        recycler.setAdapter(new SimpleListAdapter<>(Arrays.asList(getResources().getStringArray(R.array.donation_products)), this));
    }

    @NonNull @Override public DonationPresenter providePresenter() {
        return new DonationPresenter();
    }

    @NonNull public Pago getPago() {
        if (pago == null) {
            pago = new Pago(getContext().getApplicationContext());
        }
        return pago;
    }

    @Override public void onItemClick(int position, View v, String item) {
        String productKey;
        switch (position) {
            case 1:
                productKey = getString(R.string.donation_product_2);
                break;
            case 2:
                productKey = getString(R.string.donation_product_3);
                break;
            case 3:
                productKey = getString(R.string.donation_product_4);
                break;
            default:
                productKey = getString(R.string.donation_product_1);
        }
        subscription = getPago().purchaseProduct(productKey, "inapp:com.fastaccess.github:" + productKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    showErrorMessage(throwable.getMessage());
                    return null;
                })
                .subscribe(order -> showMessage(R.string.success, R.string.success_purchase_message), Throwable::printStackTrace);
    }

    @Override public void onItemLongClick(int position, View v, String item) {
        onItemClick(position, v, item);
    }

    @Override public void onDestroyView() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroyView();
    }
}

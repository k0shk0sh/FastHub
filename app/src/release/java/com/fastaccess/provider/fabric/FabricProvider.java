package com.fastaccess.provider.fabric;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fastaccess.BuildConfig;

/**
 * Created by kosh on 14/08/2017.
 */

public class FabricProvider {

    public static void initFabric(@NonNull Context context) {
        Fabric fabric = new Fabric.Builder(context)
                .kits(new Crashlytics.Builder()
                        .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                        .build())
                .debuggable(BuildConfig.DEBUG)
                .build();
        Fabric.with(fabric);
    }

    public static void logPurchase(@NonNull String productKey) {
        Answers.getInstance().logPurchase(PurchaseEvent().putItemName(productKey).putSuccess(true));
    }
}

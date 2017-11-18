package com.fastaccess.provider.fabric

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.PurchaseEvent
import com.crashlytics.android.core.CrashlyticsCore
import com.fastaccess.BuildConfig
import io.fabric.sdk.android.Fabric
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by kosh on 14/08/2017.
 */

object FabricProvider {

    fun initFabric(context: Context) {
        val fabric = Fabric.Builder(context)
                .kits(Crashlytics.Builder()
                        .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                        .build())
                .debuggable(BuildConfig.DEBUG)
                .build()
        Fabric.with(fabric)
    }

    fun logPurchase(productKey: String, price: Long? = null, priceText: String? = null) {
        val purchaseEvent = PurchaseEvent()
                .putItemName(productKey)
                .putSuccess(true)

        priceText?.let {
            purchaseEvent.putItemType(priceText)
        }
        price?.let {
            purchaseEvent.putItemPrice(BigDecimal(price).setScale(2, RoundingMode.CEILING))
        }
        Answers.getInstance().logPurchase(purchaseEvent)
    }
}

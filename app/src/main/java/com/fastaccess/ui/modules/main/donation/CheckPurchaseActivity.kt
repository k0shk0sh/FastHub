package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.os.Bundle
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.Logger
import com.fastaccess.helper.RxHelper
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import com.miguelbcr.io.rx_billing_service.entities.Purchase

/**
 * Created by kosh on 14/07/2017.
 */
class CheckPurchaseActivity : Activity() {

    override fun onStart() {
        setVisible(true)
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.e()
        RxHelper.getSingle(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .getPurchases(ProductType.IN_APP))
                .doFinally { finish() }
                .subscribe({ purchases: List<Purchase>? ->
                    purchases?.let {
                        if (!it.isEmpty()) {
                            it.filterNotNull()
                                    .map { it.sku() }
                                    .filterNot { InputHelper.isEmpty(it) }
                                    .onEach { DonateActivity.enableProduct(it, App.getInstance()) }
                        }
                    }
                }, ::println)
    }

    override fun onDestroy() {
        Logger.e()
        super.onDestroy()
    }

    override fun onBackPressed() {}
}
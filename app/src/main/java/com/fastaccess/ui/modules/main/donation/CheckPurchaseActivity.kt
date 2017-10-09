package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.modules.main.MainActivity
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import io.reactivex.Observable

/**
 * Created by kosh on 14/07/2017.
 */
class CheckPurchaseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppHelper.isGoogleAvailable(this) && !AppHelper.isEmulator()) {
            RxHelper.getObservable(Observable.fromCallable {
                val purchases = RxBillingService.getInstance(this, BuildConfig.DEBUG)
                        .getPurchases(ProductType.IN_APP)
                        .toMaybe()
                        .blockingGet(mutableListOf())
                if (!purchases.isEmpty()) {
                    purchases.filterNotNull()
                            .map { it.sku() }
                            .filterNot { !it.isNullOrBlank() }
                            .onEach { DonateActivity.enableProduct(it, App.getInstance()) }
                }
                return@fromCallable true
            }).subscribe({ startMainActivity() }, { startMainActivity() })
        } else {
            startMainActivity()
        }
    }

    override fun onBackPressed() = Unit

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }
}
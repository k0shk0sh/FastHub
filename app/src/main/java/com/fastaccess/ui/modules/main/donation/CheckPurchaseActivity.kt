package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.tasks.notification.NotificationSchedulerJobTask
import com.fastaccess.ui.modules.main.MainActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import io.reactivex.Observable

/**
 * Created by kosh on 14/07/2017.
 */
class CheckPurchaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationSchedulerJobTask.scheduleOneTimeJob(applicationContext)
        if (AppHelper.isGoogleAvailable(this)) {
            RxHelper.getObservable(Observable.fromPublisher<Boolean> { publisher ->
                try { // this could be simplified, but yet, people might download the app from outside playstore which will CRASH in somewhere.
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
                    publisher.onNext(true)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    publisher.onError(ex)
                }
                publisher.onComplete()
            }).subscribe({ /*do nothing*/ }, ::println, { startMainActivity() })
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(0, 0)
        finish()
    }
}
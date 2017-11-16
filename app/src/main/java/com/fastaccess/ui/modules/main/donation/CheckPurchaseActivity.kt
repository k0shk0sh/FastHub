package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.RxHelper
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by kosh on 14/07/2017.
 */
class CheckPurchaseActivity : Activity() {

    private var progress: ProgressDialog? = null
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress = ProgressDialog(this)
                .apply {
                    setMessage(getString(R.string.in_progress))
                    setOnCancelListener { finishActivity() }
                    show()
                }
        if (AppHelper.isGoogleAvailable(this) && !AppHelper.isEmulator()) {
            disposable = RxHelper.getObservable(Observable.fromCallable {
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
            }).subscribe({ finishActivity() }, { finishActivity() })
        } else {
            finishActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    override fun onBackPressed() = Unit

    private fun finishActivity() {
        finish()
    }

    override fun onDestroy() {
        progress?.let {
            it.dismiss()
        }
        disposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        super.onDestroy()
    }
}
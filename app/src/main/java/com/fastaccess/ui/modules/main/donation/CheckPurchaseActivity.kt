package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.RxHelper
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import es.dmoral.toasty.Toasty
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
                    setOnCancelListener { finishActivity(false) }
                    show()
                }
        if (AppHelper.isGoogleAvailable(this) && !AppHelper.isEmulator()) {
            disposable = RxHelper.getObservable(Observable.fromCallable {
                try {
                    val purchases = RxBillingService.getInstance(this, BuildConfig.DEBUG)
                            .getPurchases(ProductType.IN_APP)
                            .toMaybe()
                            .blockingGet(mutableListOf())
                    if (!purchases.isEmpty()) {
                        purchases.filterNotNull()
                                .map { it.sku() }
                                .filterNot { !it.isNullOrBlank() }
                                .onEach { DonateActivity.enableProduct(it, App.getInstance()) }
                        return@fromCallable true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@fromCallable false
            }).subscribe({ finishActivity(it) }, { finishActivity(false) })
        } else {
            finishActivity(false)
        }
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    override fun onBackPressed() = Unit

    private fun finishActivity(showMessage: Boolean) {
        if (showMessage) Toasty.success(App.getInstance(), getString(R.string.success_purchase_message), Toast.LENGTH_LONG).show()
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
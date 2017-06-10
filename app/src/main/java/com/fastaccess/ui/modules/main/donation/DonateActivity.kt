package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.PurchaseEvent
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.RxBillingServiceException
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import com.miguelbcr.io.rx_billing_service.entities.Purchase
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 10 Jun 2017, 1:04 PM
 */

class DonateActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    private var subscription: Disposable? = null

    override fun layout(): Int {
        return 0
    }

    override fun isTransparent(): Boolean {
        return false
    }

    override fun canBack(): Boolean {
        return false
    }

    override fun isSecured(): Boolean {
        return false
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle = intent.extras
        val productKey: String = bundle.getString(BundleConstant.EXTRA)
        subscription = RxHelper.getSingle<Purchase>(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .purchase(ProductType.IN_APP, productKey, "inapp:com.fastaccess.github:" + productKey))
                .subscribe { _, throwable ->
                    if (throwable == null) {
                        Answers.getInstance().logPurchase(PurchaseEvent().putItemName(productKey))
                        showMessage(R.string.success, R.string.success_purchase_message)
                        if (productKey == getString(R.string.donation_product_1)) {
                            PrefGetter.enableAmlodTheme()
                        } else {
                            PrefGetter.setProItems()
                        }
                        setResult(Activity.RESULT_OK)
                    } else {
                        if (throwable is RxBillingServiceException) {
                            val code = throwable.code
                            showErrorMessage(throwable.message!!)
                            Logger.e(code)
                            setResult(Activity.RESULT_CANCELED)
                        }
                        throwable.printStackTrace()
                    }
                    finish()
                }
    }

    override fun onDestroy() {
        if (subscription != null && !subscription?.isDisposed!!) {
            subscription?.dispose()
        }
        super.onDestroy()
    }

    companion object {
        fun start(context: Activity, product: String?) {
            val intent = Intent(context, DonateActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, product)
                    .end())
            context.startActivityForResult(intent, BundleConstant.REQUEST_CODE)
        }

        fun start(context: Fragment, product: String?) {
            val intent = Intent(context.context, DonateActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, product)
                    .end())
            context.startActivityForResult(intent, BundleConstant.REQUEST_CODE)
        }
    }
}
package com.fastaccess.ui.modules.main.donation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.provider.fabric.FabricProvider
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.RxBillingServiceError
import com.miguelbcr.io.rx_billing_service.RxBillingServiceException
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import com.miguelbcr.io.rx_billing_service.entities.Purchase
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 10 Jun 2017, 1:04 PM
 */

class DonateActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    private var subscription: Disposable? = null

    override fun layout(): Int = 0

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = false

    override fun isSecured(): Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle = intent.extras
        val productKey = bundle.getString(BundleConstant.EXTRA)
        val price = bundle.getLong(BundleConstant.EXTRA_FOUR, 0)
        val priceText = bundle.getString(BundleConstant.EXTRA_FIVE)
        subscription = RxHelper.getSingle<Purchase>(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .purchase(ProductType.IN_APP, productKey, "inapp:com.fastaccess.github:" + productKey))
                .subscribe({ p: Purchase?, throwable: Throwable? ->
                    if (throwable == null) {
                        FabricProvider.logPurchase(productKey, price, priceText)
                        showMessage(R.string.success, R.string.success_purchase_message)
                        enableProduct(productKey, applicationContext)
                        val intent = Intent()
                        intent.putExtra(BundleConstant.ITEM, productKey)
                        setResult(Activity.RESULT_OK, intent)
                    } else {
                        if (throwable is RxBillingServiceException) {
                            val code = throwable.code
                            if (code == RxBillingServiceError.ITEM_ALREADY_OWNED) {
                                enableProduct(productKey, applicationContext)
                                val intent = Intent()
                                intent.putExtra(BundleConstant.ITEM, productKey)
                                setResult(Activity.RESULT_OK, intent)
                            } else {
                                showErrorMessage(throwable.message!!)
                                Logger.e(code)
                                setResult(Activity.RESULT_CANCELED)
                            }
                        }
                        throwable.printStackTrace()
                    }
                    finish()
                })
    }

    override fun onDestroy() {
        subscription?.let { if (!it.isDisposed) it.dispose() }
        super.onDestroy()
    }

    companion object {
        fun start(context: Activity, product: String?, price: Long? = 0, priceText: String? = null) {
            val intent = Intent(context, DonateActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, product)
                    .put(BundleConstant.EXTRA_FOUR, price)
                    .put(BundleConstant.EXTRA_FIVE, priceText)
                    .end())
            context.startActivityForResult(intent, BundleConstant.REQUEST_CODE)
        }

        fun start(context: Fragment, product: String?, price: Long? = 0, priceText: String? = null) {
            val intent = Intent(context.context, DonateActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.EXTRA, product)
                    .put(BundleConstant.EXTRA_FOUR, price)
                    .put(BundleConstant.EXTRA_FIVE, priceText)
                    .end())
            context.startActivityForResult(intent, BundleConstant.REQUEST_CODE)
        }

        fun enableProduct(productKey: String, context: Context) {
            when (productKey) {
                context.getString(R.string.donation_product_3), context.getString(R.string.donation_product_4),
                context.getString(R.string.fasthub_all_features_purchase) -> {
                    PrefGetter.setProItems()
                    PrefGetter.setEnterpriseItem()
                }
                context.getString(R.string.donation_product_2), context.getString(R.string.fasthub_pro_purchase) -> PrefGetter.setProItems()
                context.getString(R.string.fasthub_enterprise_purchase) -> PrefGetter.setEnterpriseItem()
                context.getString(R.string.donation_product_1), context.getString(R.string.amlod_theme_purchase) -> PrefGetter.enableAmlodTheme()
                context.getString(R.string.midnight_blue_theme_purchase) -> PrefGetter.enableMidNightBlueTheme()
                context.getString(R.string.theme_bluish_purchase) -> PrefGetter.enableBluishTheme()
                else -> Logger.e(productKey)
            }
        }
    }
}
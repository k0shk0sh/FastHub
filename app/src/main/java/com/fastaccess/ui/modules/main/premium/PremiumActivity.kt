package com.fastaccess.ui.modules.main.premium

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.transition.TransitionManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnEditorAction
import com.airbnb.lottie.LottieAnimationView
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.provider.fabric.FabricProvider
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.main.donation.DonateActivity
import com.miguelbcr.io.rx_billing_service.RxBillingService
import com.miguelbcr.io.rx_billing_service.entities.ProductType
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by kosh on 13/07/2017.
 */
class PremiumActivity : BaseActivity<PremiumMvp.View, PremiumPresenter>(), PremiumMvp.View {

    @BindView(R.id.editText) lateinit var editText: EditText
    @BindView(R.id.viewGroup) lateinit var viewGroup: FrameLayout
    @BindView(R.id.progressLayout) lateinit var progressLayout: View
    @BindView(R.id.successActivationView) lateinit var successActivationView: LottieAnimationView
    @BindView(R.id.successActivationHolder) lateinit var successActivationHolder: View
    @BindView(R.id.proPrice) lateinit var proPriceText: TextView
    @BindView(R.id.enterprisePrice) lateinit var enterpriseText: TextView
    @BindView(R.id.buyAll) lateinit var buyAll: Button
    private var disposable: Disposable? = null
    private val allFeaturesKey by lazy { getString(R.string.fasthub_all_features_purchase) }
    private val enterpriseKey by lazy { getString(R.string.fasthub_enterprise_purchase) }
    private val proKey by lazy { getString(R.string.fasthub_pro_purchase) }

    override fun layout(): Int = R.layout.pro_features_layout

    override fun isTransparent(): Boolean = true

    override fun providePresenter(): PremiumPresenter = PremiumPresenter()

    override fun canBack(): Boolean = false

    override fun isSecured(): Boolean = true

    @OnClick(R.id.buyAll) fun onBuyAll() {
        if (!isGoogleSupported()) return
        val price = buyAll.tag as? Long?
        DonateActivity.Companion.start(this, allFeaturesKey, price, buyAll.text.toString())
    }

    @OnClick(R.id.buyPro) fun onBuyPro() {
        if (!isGoogleSupported()) return
        val price = proPriceText.tag as? Long?
        DonateActivity.Companion.start(this, proKey, price, proPriceText.text.toString())
    }

    @OnClick(R.id.buyEnterprise) fun onBuyEnterprise() {
        if (!isGoogleSupported()) return
        val price = enterpriseText.tag as? Long?
        DonateActivity.Companion.start(this, enterpriseKey, price, enterpriseText.text.toString())
    }

    @OnClick(R.id.unlock) fun onUnlock() {
        if (!isGoogleSupported()) return
        if (BuildConfig.DEBUG) {
            PrefGetter.setProItems()
            PrefGetter.setEnterpriseItem()
            onSuccessfullyActivated()
            return
        }
        val isEmpty = editText.text.isNullOrBlank()
        editText.error = if (isEmpty) getString(R.string.required_field) else null
        if (!isEmpty) {
            presenter.onCheckPromoCode(editText.text.toString())
        }
    }

    @OnEditorAction(R.id.editText) fun onEditorAction(): Boolean {
        onUnlock()
        return true
    }

    @OnClick(R.id.close) fun onClose() = finish()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buyAll.text = getString(R.string.purchase_all).replace("%price%", "$7.99")
        RxHelper.getObservable(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .getSkuDetails(ProductType.IN_APP, arrayListOf(enterpriseKey, proKey, allFeaturesKey))
                .toObservable())
                .flatMap { Observable.fromIterable(it) }
                .subscribe({
                    Logger.e(it.sku(), it.price(), it.priceCurrencyCode(), it.priceAmountMicros())
                    when (it.sku()) {
                        enterpriseKey -> {
                            enterpriseText.text = it.price()
                            enterpriseText.tag = it.priceAmountMicros()
                        }
                        proKey -> {
                            proPriceText.text = it.price()
                            proPriceText.tag = it.priceAmountMicros()
                        }
                        allFeaturesKey -> {
                            buyAll.text = getString(R.string.purchase_all).replace("%price%", it.price())
                            buyAll.tag = it.priceAmountMicros()
                        }
                    }
                }, { t -> t.printStackTrace() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            successResult()
        }
    }

    private fun successResult() {
        hideProgress()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onSuccessfullyActivated() {
        ViewHelper.hideKeyboard(editText)
        hideProgress()
        successActivationHolder.visibility = View.VISIBLE
        FabricProvider.logPurchase(InputHelper.toString(editText))
        successActivationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                showMessage(R.string.success, R.string.success)
                successResult()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}
        })
        successActivationView.playAnimation()
    }

    override fun onNoMatch() {
        hideProgress()
        showErrorMessage(getString(R.string.not_match))
    }

    override fun showProgress(resId: Int) {
        ViewHelper.hideKeyboard(editText)
        TransitionManager.beginDelayedTransition(viewGroup)
        progressLayout.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        TransitionManager.beginDelayedTransition(viewGroup)
        progressLayout.visibility = View.GONE
    }

    override fun onDestroy() {
        val disposable = disposable
        if (disposable != null && !disposable.isDisposed) disposable.dispose()
        super.onDestroy()
    }

    private fun isGoogleSupported(): Boolean {
        if (AppHelper.isGoogleAvailable(this)) {
            return true
        }
        showErrorMessage(getString(R.string.google_play_service_error))
        return false
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PremiumActivity::class.java))
        }
    }
}
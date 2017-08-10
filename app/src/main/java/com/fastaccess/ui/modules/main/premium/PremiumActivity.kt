package com.fastaccess.ui.modules.main.premium

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.transition.TransitionManager
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnEditorAction
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.PurchaseEvent
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.main.donation.DonateActivity

/**
 * Created by kosh on 13/07/2017.
 */
class PremiumActivity : BaseActivity<PremiumMvp.View, PremiumPresenter>(), PremiumMvp.View {

    @BindView(R.id.editText) lateinit var editText: EditText
    @BindView(R.id.viewGroup) lateinit var viewGroup: FrameLayout
    @BindView(R.id.progressLayout) lateinit var progressLayout: FrameLayout

    override fun layout(): Int = R.layout.pro_features_layout

    override fun isTransparent(): Boolean = true

    override fun providePresenter(): PremiumPresenter = PremiumPresenter()

    override fun canBack(): Boolean = false

    override fun isSecured(): Boolean = true

    @OnClick(R.id.buyAll) fun onBuyAll() {
        if (!isGoogleSupported()) return
        DonateActivity.Companion.start(this, getString(R.string.fasthub_all_features_purchase))
    }

    @OnClick(R.id.buyPro) fun onBuyPro() {
        if (!isGoogleSupported()) return
        DonateActivity.Companion.start(this, getString(R.string.fasthub_pro_purchase))
    }

    @OnClick(R.id.buyEnterprise) fun onBuyEnterprise() {
        if (!isGoogleSupported()) return
        DonateActivity.Companion.start(this, getString(R.string.fasthub_enterprise_purchase))
    }

    @OnClick(R.id.unlock) fun onUnlock() {
        if (!isGoogleSupported()) return
        if (BuildConfig.DEBUG) {
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

    @OnClick(R.id.close) fun onClose(): Unit = finish()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            successResult()
        }
    }

    private fun successResult() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onSuccessfullyActivated() {
        Answers.getInstance().logPurchase(PurchaseEvent().putItemName(InputHelper.toString(editText)).putSuccess(true))
        PrefGetter.setProItems()
        PrefGetter.setEnterpriseItem()
        showMessage(R.string.success, R.string.success)
        successResult()
    }

    override fun onNoMatch() {
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
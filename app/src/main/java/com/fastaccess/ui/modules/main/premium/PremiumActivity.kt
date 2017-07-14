package com.fastaccess.ui.modules.main.premium

import android.app.Activity
import android.widget.EditText
import butterknife.OnClick
import butterknife.OnEditorAction
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.main.donation.DonateActivity
import com.fastaccess.ui.widgets.bindView

/**
 * Created by kosh on 13/07/2017.
 */
class PremiumActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    val editText: EditText by bindView(R.id.editText)

    override fun layout(): Int = R.layout.pro_features_layout

    override fun isTransparent(): Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun canBack(): Boolean = false

    override fun isSecured(): Boolean = true

    @OnClick(R.id.buyAll) fun onBuyAll() {
        DonateActivity.Companion.start(this, getString(R.string.fasthub_all_features_purchase))
    }

    @OnClick(R.id.buyPro) fun onBuyPro() {
        DonateActivity.Companion.start(this, getString(R.string.fasthub_pro_purchase))
    }

    @OnClick(R.id.buyEnterprise) fun onBuyEnterprise() {
        DonateActivity.Companion.start(this, getString(R.string.fasthub_enterprise_purchase))
    }

    @OnClick(R.id.unlock) fun onUnlock() {
        val isEmpty = editText.text.isNullOrBlank()
        editText.error = if (isEmpty) getString(R.string.required_field) else null
        if (!isEmpty) {
            val enterpriseUrls = resources.getStringArray(R.array.whitelist_endpoints)
            val contains = editText.text.toString() in enterpriseUrls
            if (contains) {
                PrefGetter.setEnterpriseItem()
                PrefGetter.setProItems()
                successResult()
            } else {
                showMessage(R.string.error, R.string.not_match)
            }
        }
    }

    @OnEditorAction(R.id.editText) fun onEditorAction(): Boolean {
        onUnlock()
        return true
    }

    private fun successResult() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}
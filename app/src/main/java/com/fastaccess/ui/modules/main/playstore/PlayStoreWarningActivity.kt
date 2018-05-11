package com.fastaccess.ui.modules.main.playstore

import android.os.Bundle
import android.widget.TextView
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Hashemsergani on 21.09.17.
 */
class PlayStoreWarningActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    @OnClick(R.id.done) fun onDone() {
        PrefGetter.setPlayStoreWarningShowed()
        finish()
    }

    override fun layout(): Int = R.layout.playstore_review_layout_warning

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = false

    override fun isSecured(): Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = findViewById<TextView>(R.id.description)
        textView.post { HtmlHelper.htmlIntoTextView(textView, getString(R.string.fasthub_faq_description), textView.width) }
    }
}
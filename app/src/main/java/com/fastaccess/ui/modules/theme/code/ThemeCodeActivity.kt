package com.fastaccess.ui.modules.theme.code

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnItemSelected
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.SpinnerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.prettifier.pretty.PrettifyWebView
import com.prettifier.pretty.helper.CodeThemesHelper

/**
 * Created by Kosh on 21 Jun 2017, 2:01 PM
 */

class ThemeCodeActivity : BaseActivity<ThemeCodeMvp.View, ThemeCodePresenter>(), ThemeCodeMvp.View {

    @BindView(R.id.themesList) lateinit var spinner: Spinner
    @BindView(R.id.webView) lateinit var webView: PrettifyWebView
    @BindView(R.id.readmeLoader) lateinit var progress: ProgressBar

    override fun layout(): Int = R.layout.theme_code_layout

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): ThemeCodePresenter = ThemeCodePresenter()

    @OnClick(R.id.done) fun onSaveTheme() {
        val theme = spinner.selectedItem as String
        PrefGetter.setCodeTheme(theme)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onInitAdapter(list: List<String>) {
        val adapter = SpinnerAdapter<String>(this, list)
        spinner.adapter = adapter
        val themePosition = list.indexOf(PrefGetter.getCodeTheme())
        if (themePosition >= 0) spinner.setSelection(themePosition)
    }

    @OnItemSelected(R.id.themesList) fun onItemSelect() {
        val theme = spinner.selectedItem as String
        progress.visibility = View.VISIBLE
        webView.setThemeSource(CodeThemesHelper.CODE_EXAMPLE, theme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress.visibility = View.VISIBLE
        webView.setOnContentChangedListener(this)
        title = ""
        presenter.onLoadThemes()
    }

    override fun onContentChanged(p: Int) {
        progress.let {
            it.progress = p
            if (p == 100) it.visibility = View.GONE
        }
    }

    override fun onScrollChanged(reachedTop: Boolean, scroll: Int) {}
}

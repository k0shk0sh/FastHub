package com.fastaccess.ui.modules.theme.code

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import butterknife.OnClick
import butterknife.OnItemSelected
import com.fastaccess.R
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.SpinnerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.widgets.bindView
import com.prettifier.pretty.PrettifyWebView
import com.prettifier.pretty.helper.CodeThemesHelper

/**
 * Created by Kosh on 21 Jun 2017, 2:01 PM
 */

class ThemeCodeActivity : BaseActivity<ThemeCodeMvp.View, ThemeCodePresenter>(), ThemeCodeMvp.View {

    private val spinner: Spinner by bindView(R.id.themesList)
    private val webView: PrettifyWebView by bindView(R.id.webView)
    private val progress: ProgressBar? by bindView(R.id.readmeLoader)

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
    }

    @OnItemSelected(R.id.themesList) fun onItemSelect() {
        val theme = spinner.selectedItem as String
        progress?.visibility = View.VISIBLE
        webView.setThemeSource(CodeThemesHelper.CODE_EXAMPLE, theme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress?.visibility = View.VISIBLE
        webView.setOnContentChangedListener(this)
        title = ""
        presenter.onLoadThemes()
    }

    override fun onContentChanged(p: Int) {
        progress?.let {
            it.progress = p
            if (p == 100) it.visibility = View.GONE
        }
    }

    override fun onScrollChanged(reachedTop: Boolean, scroll: Int) {}
}

package com.fastaccess.ui.modules.repos.extras.license

import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.widgets.StateLayout
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 30 Jun 2017, 12:38 PM
 */
class RepoLicenseBottomSheet : BaseMvpBottomSheetDialogFragment<RepoLicenseMvp.View, RepoLicensePresenter>(), RepoLicenseMvp.View,
        PrettifyWebView.OnContentChangedListener {

    @State var content: String? = null


    @BindView(R.id.stateLayout) lateinit var stateLayout: StateLayout
    @BindView(R.id.readmeLoader) lateinit var loader: ProgressBar
    @BindView(R.id.webView) lateinit var webView: PrettifyWebView
    @BindView(R.id.licenseName) lateinit var licenseName: TextView

    override fun providePresenter(): RepoLicensePresenter = RepoLicensePresenter()

    override fun onLicenseLoaded(license: String) {
        this.content = license
        if (!license.isNullOrBlank()) {
            loader.isIndeterminate = false
            val licenseText = license.replace("<pre>", "<pre style='overflow: hidden;word-wrap:break-word;word-break:break-all;" +
                    "white-space:pre-line;'>")
            webView.setGithubContent("<div class='markdown-body'>$licenseText</div>", null, false)
        } else {
            hideProgress()
        }
    }

    override fun fragmentLayout(): Int = R.layout.license_viewer_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val login = it.getString(BundleConstant.EXTRA)
            val repo = it.getString(BundleConstant.ID)
            val licenseTitle = arguments?.getString(BundleConstant.EXTRA_TWO)
            licenseName.text = licenseTitle
            if (content.isNullOrBlank() && !presenter.isApiCalled) {
                presenter.onLoadLicense(login, repo)
            } else {
                content?.let { onLicenseLoaded(it) }
            }
            webView.setOnContentChangedListener(this)
        }
    }

    override fun onContentChanged(progress: Int) {
        val loader: ProgressBar? = loader
        loader?.let {
            it.progress = progress
            if (progress == 100) {
                it.visibility = View.GONE
                hideProgress()
            }
        }
    }

    override fun showProgress(@StringRes resId: Int) {
        loader.visibility = View.VISIBLE
        loader.isIndeterminate = true
        stateLayout.showProgress()
    }

    override fun hideProgress() {
        loader.visibility = View.GONE
        stateLayout.hideProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun onScrollChanged(reachedTop: Boolean, scroll: Int) {}

    companion object {
        fun newInstance(login: String, repo: String, license: String): RepoLicenseBottomSheet {
            val view = RepoLicenseBottomSheet()
            view.arguments = Bundler.start()
                    .put(BundleConstant.ID, repo)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, license)
                    .end()
            return view
        }
    }
}
package com.fastaccess.ui.modules.reviews.changes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import butterknife.BindView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.ReviewRequestModel
import com.fastaccess.helper.*
import com.fastaccess.provider.theme.ThemeEngine
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.widgets.dialog.ProgressDialogFragment

/**
 * Created by Kosh on 25 Jun 2017, 1:25 AM
 */
class ReviewChangesActivity : BaseActivity<ReviewChangesMvp.View, ReviewChangesPresenter>(), ReviewChangesMvp.View {


    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.reviewMethod) lateinit var spinner: Spinner
    @BindView(R.id.editText) lateinit var editText: TextInputLayout

    @State var reviewRequest: ReviewRequestModel? = null
    @State var repoId: String? = null
    @State var owner: String? = null
    @State var number: Long? = null
    @State var isProgressShowing: Boolean = false
    @State var isClosed: Boolean = false

    override fun layout(): Int = R.layout.add_review_dialog_layout

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): ReviewChangesPresenter = ReviewChangesPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeEngine.applyDialogTheme(this)
        super.onCreate(savedInstanceState)
        setToolbarIcon(R.drawable.ic_clear)
        val bundle = intent.extras!!
        reviewRequest = bundle.getParcelable(BundleConstant.EXTRA)
        repoId = bundle.getString(BundleConstant.EXTRA_TWO)
        owner = bundle.getString(BundleConstant.EXTRA_THREE)
        number = bundle.getLong(BundleConstant.ID)
        isClosed = bundle.getBoolean(BundleConstant.EXTRA_FIVE)
        val isAuthor = bundle.getBoolean(BundleConstant.EXTRA_FOUR)
        if (isAuthor || isClosed) {
            spinner.setSelection(2, true)
            spinner.isEnabled = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.submit -> {
                if (spinner.selectedItemPosition != 0 && editText.editText?.text.isNullOrEmpty()) {
                    editText.error = getString(R.string.required_field)
                } else {
                    editText.error = null
                    presenter.onSubmit(reviewRequest!!, repoId!!, owner!!, number!!, InputHelper.toString(editText), spinner.selectedItem as String)
                }
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSuccessfullySubmitted() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onErrorSubmitting() {
        showErrorMessage(getString(R.string.network_error))
    }

    override fun showProgress(@StringRes resId: Int) {
        var msg = getString(R.string.in_progress)
        if (resId != 0) {
            msg = getString(resId)
        }
        if (!isProgressShowing && !isFinishing) {
            var fragment = AppHelper.getFragmentByTag(supportFragmentManager,
                    ProgressDialogFragment.TAG) as ProgressDialogFragment?
            if (fragment == null) {
                isProgressShowing = true
                fragment = ProgressDialogFragment.newInstance(msg, false)
                fragment.show(supportFragmentManager, ProgressDialogFragment.TAG)
            }
        }
    }

    override fun hideProgress() {
        val fragment = AppHelper.getFragmentByTag(supportFragmentManager, ProgressDialogFragment.TAG) as ProgressDialogFragment?
        if (fragment != null) {
            isProgressShowing = false
            fragment.dismiss()
        }
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    companion object {
        fun startForResult(activity: Activity, view: View, reviewChanges: ReviewRequestModel, repoId: String, owner: String, number: Long,
                           isAuthor: Boolean, isEnterprise: Boolean, isClosed: Boolean) {
            val bundle = Bundler.start()
                    .put(BundleConstant.EXTRA, reviewChanges)
                    .put(BundleConstant.EXTRA_TWO, repoId)
                    .put(BundleConstant.EXTRA_THREE, owner)
                    .put(BundleConstant.EXTRA_FOUR, isAuthor)
                    .put(BundleConstant.ID, number)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .put(BundleConstant.EXTRA_FIVE, isClosed)
                    .end()
            val intent = Intent(activity, ReviewChangesActivity::class.java)
            intent.putExtras(bundle)
            ActivityHelper.startReveal(activity, intent, view, BundleConstant.REVIEW_REQUEST_CODE)
        }
    }


}
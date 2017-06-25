package com.fastaccess.ui.modules.reviews.changes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.ReviewRequestModel
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.provider.theme.ThemeEngine
import com.fastaccess.ui.base.BaseActivity

/**
 * Created by Kosh on 25 Jun 2017, 1:25 AM
 */
class ReviewChangesActivity : BaseActivity<ReviewChangesMvp.View, ReviewChangesPresenter>(), ReviewChangesMvp.View {

    val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) as Toolbar }
    val spinner: Spinner by lazy { findViewById(R.id.reviewMethod) as Spinner }
    val editText: TextInputLayout by lazy { findViewById(R.id.editText) as TextInputLayout }

    @State var reviewRequest: ReviewRequestModel? = null
    @State var repoId: String? = null
    @State var owner: String? = null
    @State var number: Long? = null

    override fun layout(): Int = R.layout.add_review_dialog_layout

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): ReviewChangesPresenter = ReviewChangesPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeEngine.applyDialogTheme(this)
        super.onCreate(savedInstanceState)
        val bundle = intent.extras!!
        reviewRequest = bundle.getParcelable(BundleConstant.EXTRA)
        repoId = bundle.getString(BundleConstant.EXTRA_TWO)
        owner = bundle.getString(BundleConstant.EXTRA_THREE)
        number = bundle.getLong(BundleConstant.ID)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.submit -> {
                if (editText.editText?.text.isNullOrEmpty()) {
                    editText.error = getString(R.string.required_field)
                } else {
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

    companion object {
        /**
         * val repoId = bundle.getString(BundleConstant.EXTRA_TWO)
         * val owner = bundle.getString(BundleConstant.EXTRA_THREE)
         * val number = bundle.getLong(BundleConstant.ID)
         */
        fun startForResult(activity: Activity, view: View, reviewChanges: ReviewRequestModel, repoId: String, owner: String, number: Long) {
            val bundle = Bundler.start()
                    .put(BundleConstant.EXTRA, reviewChanges)
                    .put(BundleConstant.EXTRA_TWO, repoId)
                    .put(BundleConstant.EXTRA_THREE, owner)
                    .put(BundleConstant.ID, number)
                    .end()
            val intent = Intent(activity, ReviewChangesActivity::class.java)
            intent.putExtras(bundle)
            ActivityHelper.startReveal(activity, intent, view, BundleConstant.REVIEW_REQUEST_CODE)
        }
    }


}
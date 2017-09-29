package com.fastaccess.ui.modules.repos.projects.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import com.airbnb.lottie.LottieAnimationView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.ui.modules.user.UserPagerActivity
import com.fastaccess.ui.widgets.CardsPagerTransformerBasic

/**
 * Created by Hashemsergani on 11.09.17.
 */

class ProjectPagerActivity : BaseActivity<ProjectPagerMvp.View, ProjectPagerPresenter>(), ProjectPagerMvp.View {

    @BindView(R.id.pager) lateinit var pager: ViewPager
    @BindView(R.id.loading) lateinit var loading: LottieAnimationView
    @State var isProgressShowing = false

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): ProjectPagerPresenter = ProjectPagerPresenter()

    override fun onInitPager(list: List<ProjectColumnModel>) {
        hideProgress()
        pager.adapter = FragmentsPagerAdapter(supportFragmentManager, FragmentPagerAdapterModel
                .buildForProjectColumns(list, presenter.viewerCanUpdate))
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

    override fun showProgress(resId: Int) {
        isProgressShowing = true
        loading.visibility = View.VISIBLE
        loading.playAnimation()
    }

    override fun hideProgress() {
        isProgressShowing = false
        loading.cancelAnimation()
        loading.visibility = View.GONE
    }

    override fun layout(): Int = R.layout.projects_activity_layout

    override fun isTransparent(): Boolean = true

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                val repoId = presenter.repoId
                if (repoId != null && !repoId.isNullOrBlank()) {
                    if (!presenter.login.isBlank()) {
                        val nameParse = NameParser("")
                        nameParse.name = presenter.repoId
                        nameParse.username = presenter.login
                        nameParse.isEnterprise = isEnterprise
                        RepoPagerActivity.startRepoPager(this, nameParse)
                    }
                } else if (!presenter.login.isBlank()) {
                    UserPagerActivity.startActivity(this, presenter.login, true, isEnterprise, 0)
                }
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isProgressShowing) {
            showProgress(0)
        } else {
            hideProgress()
        }
        pager.clipToPadding = false
        val partialWidth = resources.getDimensionPixelSize(R.dimen.spacing_xs_large)
        val pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        val pagerPadding = partialWidth + pageMargin
        pager.pageMargin = pageMargin
        pager.setPageTransformer(true, CardsPagerTransformerBasic(4, 10))
        pager.setPadding(pagerPadding, pagerPadding, pagerPadding, pagerPadding)

        if (savedInstanceState == null) {
            presenter.onActivityCreated(intent)
        } else if (presenter.getColumns().isEmpty() && !presenter.isApiCalled) {
            presenter.onRetrieveColumns()
        } else {
            onInitPager(presenter.getColumns())
        }
        if (presenter.repoId.isNullOrBlank()) {
            toolbar?.subtitle = presenter.login
        } else {
            toolbar?.subtitle = "${presenter.login}/${presenter.repoId}"
        }
    }

    override fun onDeletePage(model: ProjectColumnModel) {
        presenter.getColumns().remove(model)
        onInitPager(presenter.getColumns())
    }

    companion object {
        fun startActivity(context: Context, login: String, repoId: String? = null, projectId: Long, isEnterprise: Boolean = false) {
            context.startActivity(getIntent(context, login, repoId, projectId, isEnterprise))
        }

        fun getIntent(context: Context, login: String, repoId: String? = null, projectId: Long, isEnterprise: Boolean = false): Intent {
            val intent = Intent(context, ProjectPagerActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ID, projectId)
                    .put(BundleConstant.ITEM, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .end())
            return intent
        }

    }

}
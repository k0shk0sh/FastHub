package com.fastaccess.ui.modules.repos.projects.list.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import com.airbnb.lottie.LottieAnimationView
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.ui.widgets.CardsPagerTransformerBasic

/**
 * Created by Hashemsergani on 11.09.17.
 */

class ProjectPagerActivity : BaseActivity<ProjectPagerMvp.View, ProjectPagerPresenter>(), ProjectPagerMvp.View {


    @BindView(R.id.pager) lateinit var pager: ViewPager
    @BindView(R.id.loading) lateinit var loading: LottieAnimationView

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): ProjectPagerPresenter = ProjectPagerPresenter()

    override fun onInitPager(list: List<ProjectColumnModel>) {
        hideProgress()
        pager.adapter = FragmentsPagerAdapter(supportFragmentManager, FragmentPagerAdapterModel.buildForProjectColumns(list))
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
        loading.visibility = View.VISIBLE
        loading.playAnimation()
    }

    override fun hideProgress() {
        loading.cancelAnimation()
        loading.visibility = View.GONE
    }

    override fun layout(): Int = R.layout.projects_activity_layout

    override fun isTransparent(): Boolean = true

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                if (!presenter.login.isBlank() && !presenter.repoId.isBlank()) {
                    val nameParse = NameParser("")
                    nameParse.name = presenter.repoId
                    nameParse.username = presenter.login
                    nameParse.isEnterprise = isEnterprise
                    RepoPagerActivity.startRepoPager(this, nameParse)
                }
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pager.clipToPadding = false
        val pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        pager.pageMargin = pageMargin
        pager.setPageTransformer(true, CardsPagerTransformerBasic(4, 10))
        pager.setPadding(pageMargin, pageMargin, pageMargin, pageMargin)

        if (savedInstanceState == null) {
            presenter.onActivityCreated(intent)
        } else if (presenter.getColumns().isEmpty() && !presenter.isApiCalled) {
            presenter.onRetrieveColumns()
        } else {
            onInitPager(presenter.getColumns())
        }
    }

    companion object {
        fun startActivity(context: Context, login: String, repoId: String, projectId: Long) {
            context.startActivity(getIntent(context, login, repoId, projectId))
        }

        fun getIntent(context: Context, login: String, repoId: String, projectId: Long): Intent {
            val intent = Intent(context, ProjectPagerActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ID, projectId)
                    .put(BundleConstant.ITEM, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .end())
            return intent
        }

    }


}
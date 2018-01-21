package com.fastaccess.ui.modules.profile.org.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import butterknife.BindView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.projects.RepoProjectsFragmentPager

/**
 * Created by Hashemsergani on 24.09.17.
 */

class OrgProjectActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    @State var org: String? = null

    @BindView(R.id.appbar) lateinit var appBar: AppBarLayout

    override fun layout(): Int = R.layout.activity_fragment_layout

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBar.elevation = 0f
        appBar.stateListAnimator = null
        if (savedInstanceState == null) {
            org = intent.extras.getString(BundleConstant.ITEM)
            val org = org
            if (org != null) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, RepoProjectsFragmentPager.newInstance(org),
                                RepoProjectsFragmentPager.TAG)
                        .commit()
            }
        }
        toolbar?.apply { subtitle = org }
    }

    companion object {
        fun startActivity(context: Context, org: String, isEnterprise: Boolean) {
            val intent = Intent(context, OrgProjectActivity::class.java)
            intent.putExtras(Bundler.start().put(BundleConstant.ITEM, org)
                    .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                    .end())
            context.startActivity(intent)
        }
    }
}
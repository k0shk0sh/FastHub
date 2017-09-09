package com.fastaccess.ui.modules.repos.projects

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.ViewPagerView

/**
 * Created by kosh on 09/09/2017.
 */
class RepoProjectsFragmentPager : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    @BindView(R.id.tabs) lateinit var tabs: TabLayout
    @BindView(R.id.pager) lateinit var pager: ViewPagerView

    override fun fragmentLayout(): Int = R.layout.centered_tabbed_viewpager

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = FragmentsPagerAdapter(childFragmentManager, FragmentPagerAdapterModel.buildForRepoProjects(context,
                arguments.getString(BundleConstant.EXTRA), arguments.getString(BundleConstant.ID)))
        tabs.setupWithViewPager(pager)
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    companion object {
        val TAG = RepoProjectsFragmentPager::class.java.simpleName
        fun newInstance(login: String, repoId: String): RepoProjectsFragmentPager {
            val fragment = RepoProjectsFragmentPager()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .end()
            return fragment
        }
    }
}
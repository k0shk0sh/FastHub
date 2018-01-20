package com.fastaccess.ui.modules.repos.projects

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.data.dao.TabsCountStateModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.ViewHelper
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerMvp
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.ViewPagerView

/**
 * Created by kosh on 09/09/2017.
 */
class RepoProjectsFragmentPager : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(), RepoPagerMvp.TabsBadgeListener {

    @BindView(R.id.tabs) lateinit var tabs: TabLayout
    @BindView(R.id.pager) lateinit var pager: ViewPagerView
    private var counts: HashSet<TabsCountStateModel>? = null

    override fun fragmentLayout(): Int = R.layout.centered_tabbed_viewpager

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (counts?.isNotEmpty() == true) {
            outState.putSerializable("counts", counts)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = FragmentsPagerAdapter(childFragmentManager, FragmentPagerAdapterModel.buildForRepoProjects(context!!,
                arguments!!.getString(BundleConstant.ID), arguments!!.getString(BundleConstant.EXTRA)))
        tabs.setupWithViewPager(pager)
        if (savedInstanceState != null) {
            @Suppress("UNCHECKED_CAST")
            counts = savedInstanceState.getSerializable("counts") as? HashSet<TabsCountStateModel>?
            counts?.let {
                if (!it.isEmpty()) it.onEach { updateCount(it) }
            }
        } else {
            counts = hashSetOf()
        }
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onSetBadge(tabIndex: Int, count: Int) {
        val model = TabsCountStateModel()
        model.tabIndex = tabIndex
        model.count = count
        counts?.add(model)
        tabs.let { updateCount(model) }
    }

    private fun updateCount(model: TabsCountStateModel) {
        val tv = ViewHelper.getTabTextView(tabs, model.tabIndex)
        tv.text = SpannableBuilder.builder()
                .append(if (model.tabIndex == 0) getString(R.string.opened) else getString(R.string.closed))
                .append("   ")
                .append("(")
                .bold(model.count.toString())
                .append(")")
    }

    companion object {
        val TAG = RepoProjectsFragmentPager::class.java.simpleName
        fun newInstance(login: String, repoId: String? = null): RepoProjectsFragmentPager {
            val fragment = RepoProjectsFragmentPager()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .end()
            return fragment
        }
    }
}
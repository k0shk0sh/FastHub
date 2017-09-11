package com.fastaccess.ui.modules.repos.projects.list

import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.ProjectsModel
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Logger
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.projects.list.details.ProjectPagerActivity
import java.util.*

/**
 * Created by kosh on 09/09/2017.
 */
class RepoProjectPresenter : BasePresenter<RepoProjectMvp.View>(), RepoProjectMvp.Presenter {

    private val projects = ArrayList<ProjectsModel>()
    private var page: Int = 0
    private var previousTotal: Int = 0
    private var lastPage = Integer.MAX_VALUE
    @com.evernote.android.state.State var login: String = ""
    @com.evernote.android.state.State var repoId: String = ""

    override fun onItemClick(position: Int, v: View, item: ProjectsModel) {
        ProjectPagerActivity.startActivity(v.context, login, repoId, item.id)
    }

    override fun onItemLongClick(position: Int, v: View?, item: ProjectsModel?) {}

    override fun onFragmentCreate(bundle: Bundle?) {
        bundle?.let {
            repoId = it.getString(BundleConstant.ID)
            login = it.getString(BundleConstant.EXTRA)
        }
    }

    override fun getProjects(): ArrayList<ProjectsModel> = projects

    override fun getCurrentPage(): Int = page

    override fun getPreviousTotal(): Int = previousTotal

    override fun setCurrentPage(page: Int) {
        this.page = page
    }

    override fun setPreviousTotal(previousTotal: Int) {
        this.previousTotal = previousTotal
    }

    override fun onCallApi(page: Int, parameter: IssueState?): Boolean {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE
            sendToView { view -> view.getLoadMore().reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView({ it.hideProgress() })
            return false
        }
        currentPage = page
        makeRestCall(RestProvider.getProjectsService(isEnterprise)
                .getRepoProjects(login, repoId, parameter?.name, page), { response ->
            lastPage = response.last
            Logger.e(response.items as List<Any>?)
            sendToView({ it.onNotifyAdapter(response.items, page) })
        })
        return true
    }
}
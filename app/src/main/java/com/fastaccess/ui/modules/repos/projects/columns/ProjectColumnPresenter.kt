package com.fastaccess.ui.modules.repos.projects.columns

import android.view.View
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.helper.Logger
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import java.util.*

/**
 * Created by Hashemsergani on 11.09.17.
 */

class ProjectColumnPresenter : BasePresenter<ProjectColumnMvp.View>(), ProjectColumnMvp.Presenter {


    private val projects = ArrayList<ProjectCardModel>()
    private var page: Int = 0
    private var previousTotal: Int = 0
    private var lastPage = Integer.MAX_VALUE

    override fun onItemClick(position: Int, v: View?, item: ProjectCardModel?) {}

    override fun onItemLongClick(position: Int, v: View?, item: ProjectCardModel?) {}

    override fun getCards(): ArrayList<ProjectCardModel> = projects

    override fun getCurrentPage(): Int = page

    override fun getPreviousTotal(): Int = previousTotal

    override fun setCurrentPage(page: Int) {
        this.page = page
    }

    override fun setPreviousTotal(previousTotal: Int) {
        this.previousTotal = previousTotal
    }

    override fun onCallApi(page: Int, parameter: Long?): Boolean {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE
            sendToView { view -> view.getLoadMore().reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView({ it.hideProgress() })
            return false
        }
        currentPage = page
        makeRestCall(RestProvider.getProjectsService(isEnterprise).getProjectCards(parameter!!, page),
                { response ->
                    lastPage = response.last
                    Logger.e(response.items as List<Any>?)
                    sendToView({ it.onNotifyAdapter(response.items, page) })
                })
        return true
    }

}
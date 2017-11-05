package com.fastaccess.ui.modules.repos.projects.details

import android.content.Intent
import com.fastaccess.R
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.data.dao.model.Login
import com.fastaccess.helper.BundleConstant
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import retrofit2.Response

/**
 * Created by Hashemsergani on 11.09.17.
 */
class ProjectPagerPresenter : BasePresenter<ProjectPagerMvp.View>(), ProjectPagerMvp.Presenter {

    private val columns = arrayListOf<ProjectColumnModel>()
    @com.evernote.android.state.State var projectId: Long = -1
    @com.evernote.android.state.State var repoId: String? = null
    @com.evernote.android.state.State var login: String = ""
    @com.evernote.android.state.State var viewerCanUpdate: Boolean = false

    override fun onError(throwable: Throwable) {
        val code = RestProvider.getErrorCode(throwable)
        if (code == 404) {
            sendToView { it.onOpenUrlInBrowser() }
        }
        super.onError(throwable)
    }

    override fun getColumns(): ArrayList<ProjectColumnModel> = columns

    override fun onRetrieveColumns() {
        val repoId = repoId
        if (repoId != null && !repoId.isNullOrBlank()) {
            makeRestCall(Observable.zip(RestProvider.getProjectsService(isEnterprise).getProjectColumns(projectId),
                    RestProvider.getRepoService(isEnterprise).isCollaborator(login, repoId, Login.getUser().login),
                    BiFunction { items: Pageable<ProjectColumnModel>, response: Response<Boolean> ->
                        viewerCanUpdate = response.code() == 204
                        return@BiFunction items
                    })
                    .flatMap {
                        if (it.items != null) {
                            return@flatMap Observable.just(it.items)
                        }
                        return@flatMap Observable.just(listOf<ProjectColumnModel>())
                    },
                    { t ->
                        columns.clear()
                        columns.addAll(t)
                        sendToView { it.onInitPager(columns) }
                    })
        } else {
            makeRestCall(RestProvider.getProjectsService(isEnterprise).getProjectColumns(projectId)
                    .flatMap {
                        if (it.items != null) {
                            return@flatMap Observable.just(it.items)
                        }
                        return@flatMap Observable.just(listOf<ProjectColumnModel>())
                    },
                    { t ->
                        columns.clear()
                        columns.addAll(t)
                        sendToView { it.onInitPager(columns) }
                    })
        }
    }

    override fun onActivityCreated(intent: Intent?) {
        intent?.let {
            it.extras?.let {
                projectId = it.getLong(BundleConstant.ID)
                repoId = it.getString(BundleConstant.ITEM)
                login = it.getString(BundleConstant.EXTRA)
            }
        }
        if (columns.isEmpty()) {
            if (projectId > 0)
                onRetrieveColumns()
            else
                sendToView { it.showMessage(R.string.error, R.string.unexpected_error) }
        } else {
            sendToView { it.onInitPager(columns) }
        }
    }
}
package com.fastaccess.ui.modules.repos.projects.list

import android.os.Bundle
import android.view.View
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Logger
import com.fastaccess.provider.rest.ApolloProdivder
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.projects.details.ProjectPagerActivity
import github.OrgProjectsClosedQuery
import github.OrgProjectsOpenQuery
import github.RepoProjectsClosedQuery
import github.RepoProjectsOpenQuery
import io.reactivex.Observable

/**
 * Created by kosh on 09/09/2017.
 */
class RepoProjectPresenter : BasePresenter<RepoProjectMvp.View>(), RepoProjectMvp.Presenter {

    private val projects = arrayListOf<RepoProjectsOpenQuery.Node>()
    private var page: Int = 0
    private var previousTotal: Int = 0
    private var lastPage = Integer.MAX_VALUE
    @com.evernote.android.state.State var login: String = ""
    @com.evernote.android.state.State var repoId: String? = null
    var count: Int = 0
    val pages = arrayListOf<String>()

    override fun onItemClick(position: Int, v: View, item: RepoProjectsOpenQuery.Node) {
        item.databaseId()?.let {
            ProjectPagerActivity.startActivity(v.context, login, repoId, it.toLong(), isEnterprise)
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: RepoProjectsOpenQuery.Node?) {}

    override fun onFragmentCreate(bundle: Bundle?) {
        bundle?.let {
            repoId = it.getString(BundleConstant.ID)
            login = it.getString(BundleConstant.EXTRA)
        }
    }

    override fun getProjects(): ArrayList<RepoProjectsOpenQuery.Node> = projects

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
        if (page > lastPage || lastPage == 0 || parameter == null) {
            sendToView({ it.hideProgress() })
            return false
        }
        currentPage = page
        Logger.e(login)
        val repoId = repoId
        val apollo = ApolloProdivder.getApollo(isEnterprise)
        if (repoId != null && !repoId.isNullOrBlank()) {
            if (parameter == IssueState.open) {
                val query = RepoProjectsOpenQuery.builder()
                        .name(repoId)
                        .owner(login)
                        .page(getPage())
                        .build()
                makeRestCall(Rx2Apollo.from(apollo.query(query))
                        .flatMap {
                            val list = arrayListOf<RepoProjectsOpenQuery.Node>()
                            it.data()?.repository()?.let {
                                it.projects().let {
                                    lastPage = if (it.pageInfo().hasNextPage()) Int.MAX_VALUE else 0
                                    pages.clear()
                                    count = it.totalCount()
                                    it.edges()?.let {
                                        pages.addAll(it.map { it.cursor() })
                                    }
                                    it.nodes()?.let {
                                        list.addAll(it)
                                    }
                                }
                            }
                            return@flatMap Observable.just(list)
                        },
                        {
                            sendToView({ v ->
                                v.onNotifyAdapter(it, page)
                                if (page == 1) v.onChangeTotalCount(count)
                            })
                        })
            } else {
                val query = RepoProjectsClosedQuery.builder()
                        .name(repoId)
                        .owner(login)
                        .page(getPage())
                        .build()
                makeRestCall(Rx2Apollo.from(apollo.query(query))
                        .flatMap {
                            val list = arrayListOf<RepoProjectsOpenQuery.Node>()
                            it.data()?.repository()?.let {
                                it.projects().let {
                                    lastPage = if (it.pageInfo().hasNextPage()) Int.MAX_VALUE else 0
                                    pages.clear()
                                    count = it.totalCount()
                                    it.edges()?.let {
                                        pages.addAll(it.map { it.cursor() })
                                    }
                                    it.nodes()?.let {
                                        val toConvert = arrayListOf<RepoProjectsOpenQuery.Node>()
                                        it.onEach {
                                            val columns = RepoProjectsOpenQuery.Columns(it.columns().__typename(), it.columns().totalCount())
                                            val node = RepoProjectsOpenQuery.Node(it.__typename(), it.name(), it.number(), it.body(),
                                                    it.createdAt(), it.id(), it.viewerCanUpdate(), columns, it.databaseId())
                                            toConvert.add(node)
                                        }
                                        list.addAll(toConvert)
                                    }
                                }
                            }
                            return@flatMap Observable.just(list)
                        },
                        {
                            sendToView({ v ->
                                v.onNotifyAdapter(it, page)
                                if (page == 1) v.onChangeTotalCount(count)
                            })
                        })
            }
        } else {
            if (parameter == IssueState.open) {
                val query = OrgProjectsOpenQuery.builder()
                        .owner(login)
                        .page(getPage())
                        .build()
                makeRestCall(Rx2Apollo.from(apollo.query(query))
                        .flatMap {
                            val list = arrayListOf<RepoProjectsOpenQuery.Node>()
                            it.data()?.organization()?.let {
                                it.projects().let {
                                    lastPage = if (it.pageInfo().hasNextPage()) Int.MAX_VALUE else 0
                                    pages.clear()
                                    count = it.totalCount()
                                    it.edges()?.let {
                                        pages.addAll(it.map { it.cursor() })
                                    }
                                    it.nodes()?.let {
                                        val toConvert = arrayListOf<RepoProjectsOpenQuery.Node>()
                                        it.onEach {
                                            val columns = RepoProjectsOpenQuery.Columns(it.columns().__typename(), it.columns().totalCount())
                                            val node = RepoProjectsOpenQuery.Node(it.__typename(), it.name(), it.number(), it.body(),
                                                    it.createdAt(), it.id(), it.viewerCanUpdate(), columns, it.databaseId())
                                            toConvert.add(node)
                                        }
                                        list.addAll(toConvert)
                                    }
                                }
                            }
                            return@flatMap Observable.just(list)
                        },
                        {
                            sendToView({ v ->
                                v.onNotifyAdapter(it, page)
                                if (page == 1) v.onChangeTotalCount(count)
                            })
                        })
            } else {
                val query = OrgProjectsClosedQuery.builder()
                        .owner(login)
                        .page(getPage())
                        .build()
                makeRestCall(Rx2Apollo.from(apollo.query(query))
                        .flatMap {
                            val list = arrayListOf<RepoProjectsOpenQuery.Node>()
                            it.data()?.organization()?.let {
                                it.projects().let {
                                    lastPage = if (it.pageInfo().hasNextPage()) Int.MAX_VALUE else 0
                                    pages.clear()
                                    count = it.totalCount()
                                    it.edges()?.let {
                                        pages.addAll(it.map { it.cursor() })
                                    }
                                    it.nodes()?.let {
                                        val toConvert = arrayListOf<RepoProjectsOpenQuery.Node>()
                                        it.onEach {
                                            val columns = RepoProjectsOpenQuery.Columns(it.columns().__typename(), it.columns().totalCount())
                                            val node = RepoProjectsOpenQuery.Node(it.__typename(), it.name(), it.number(), it.body(),
                                                    it.createdAt(), it.id(), it.viewerCanUpdate(), columns, it.databaseId())
                                            toConvert.add(node)
                                        }
                                        list.addAll(toConvert)
                                    }
                                }
                            }
                            return@flatMap Observable.just(list)
                        },
                        {
                            sendToView({ v ->
                                v.onNotifyAdapter(it, page)
                                if (page == 1) v.onChangeTotalCount(count)
                            })
                        })
            }
        }
        return true
    }

    private fun getPage(): String? = if (pages.isNotEmpty()) pages.last() else null
}
package com.fastaccess.ui.modules.repos.extras.branches

import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.data.dao.Pageable
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * Created by Kosh on 06 Jul 2017, 9:14 PM
 */
class BranchesPresenter : BasePresenter<BranchesMvp.View>(), BranchesMvp.Presenter {
    private var page: Int = 0
    private var previousTotal: Int = 0
    private var lastPage = Integer.MAX_VALUE
    @com.evernote.android.state.State var login: String? = null
    @com.evernote.android.state.State var repoId: String? = null

    var branches = ArrayList<BranchesModel>()


    override fun onFragmentCreated(bundle: Bundle) {
        login = bundle.getString(BundleConstant.EXTRA)
        repoId = bundle.getString(BundleConstant.ID)
        if (branches.isEmpty()) {
            onCallApi(1, null)
        }
    }

    private fun getObservable(login: String, repoId: String, page: Int): Observable<ArrayList<BranchesModel>> {
        return RxHelper.getObserver(Observable.zip(
                RestProvider.getRepoService(isEnterprise()).getBranches(login, repoId, page),
                RestProvider.getRepoService(isEnterprise()).getTags(login, repoId, page),
                BiFunction({ branchPageable: Pageable<BranchesModel>?, tags: Pageable<BranchesModel>? ->
                    val branchesModels = ArrayList<BranchesModel>()
                    if (branchPageable != null) {
                        if (tags != null) {
                            if (branchPageable.last > tags.last) {
                                lastPage = branchPageable.last
                            } else {
                                lastPage = tags.last
                            }
                        }
                    } else if (tags != null) {
                        lastPage = tags.last
                    }

                    if (branchPageable != null && branchPageable.items != null) {
                        branchesModels.addAll(branchPageable.items.map {
                            it.isTag = false
                            return@map it
                        })
                    }
                    if (tags != null && tags.items != null) {
                        branchesModels.addAll(tags.items.map {
                            it.isTag = true
                            return@map it
                        })
                    }
                    return@BiFunction branchesModels
                })))
    }

    override fun onItemClick(position: Int, v: View?, item: BranchesModel?) {
        sendToView({ it.onBranchSelected(item) })
    }

    override fun onItemLongClick(position: Int, v: View?, item: BranchesModel?) {}

    override fun getCurrentPage(): Int = page

    override fun getPreviousTotal(): Int = previousTotal

    override fun setCurrentPage(page: Int) {
        this.page = page
    }

    override fun setPreviousTotal(previousTotal: Int) {
        this.previousTotal = previousTotal
    }

    override fun onCallApi(page: Int, parameter: Any?) {
        if (login.isNullOrEmpty() || repoId.isNullOrEmpty()) {
            sendToView({ it.hideProgress() })
            return
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE
            sendToView({ it.getLoadMore().reset() })
        }
        if (page > lastPage || lastPage == 0) {
            sendToView({ it.hideProgress() })
            return
        }
        currentPage = page
        val observable = getObservable(login!!, repoId!!, page)
        makeRestCall<ArrayList<BranchesModel>>(observable, { models -> sendToView({ it.onNotifyAdapter(models, page) }) })
    }

}
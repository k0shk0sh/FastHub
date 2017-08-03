package com.fastaccess.ui.modules.repos.extras.branches

import android.os.Bundle
import android.view.View
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.data.dao.Pageable
import com.fastaccess.helper.BundleConstant
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Kosh on 06 Jul 2017, 9:14 PM
 */
class BranchesPresenter : BasePresenter<BranchesMvp.View>(), BranchesMvp.Presenter {
    private var page: Int = 0
    private var previousTotal: Int = 0
    private var lastPage = Integer.MAX_VALUE
    @com.evernote.android.state.State var login: String? = null
    @com.evernote.android.state.State var repoId: String? = null
    @com.evernote.android.state.State var isBranch: Boolean = true

    var branches = ArrayList<BranchesModel>()


    override fun onFragmentCreated(bundle: Bundle) {
        login = bundle.getString(BundleConstant.EXTRA)
        repoId = bundle.getString(BundleConstant.ID)
        isBranch = bundle.getBoolean(BundleConstant.EXTRA_TYPE)
        if (branches.isEmpty()) {
            onCallApi(1, null)
        }
    }

    private fun callApi(login: String, repoId: String, page: Int) {
        val observable = if (!isBranch) RestProvider.getRepoService(isEnterprise)
                .getTags(login, repoId, page) else RestProvider.getRepoService(isEnterprise)
                .getBranches(login, repoId, page)
        return makeRestCall(observable
                .flatMap({ t: Pageable<BranchesModel>? ->
                    val list = ArrayList<BranchesModel>()
                    if (t != null) {
                        lastPage = t.last
                        t.items.onEach {
                            it.isTag = !isBranch
                            list.add(it)
                        }
                    }
                    return@flatMap Observable.just(list)
                }), { items -> sendToView { v -> v.onNotifyAdapter(items, page) } })
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

    override fun onCallApi(page: Int, parameter: Boolean?): Boolean {
        if (login.isNullOrEmpty() || repoId.isNullOrEmpty()) {
            sendToView({ it.hideProgress() })
            return false
        }
        if (page == 1) {
            lastPage = Integer.MAX_VALUE
            sendToView({ it.getLoadMore().reset() })
        }
        if (page > lastPage || lastPage == 0) {
            sendToView({ it.hideProgress() })
            return false
        }
        currentPage = page
        callApi(login!!, repoId!!, page)
        return true
    }

}
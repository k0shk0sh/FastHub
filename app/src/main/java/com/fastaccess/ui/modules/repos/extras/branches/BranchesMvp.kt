package com.fastaccess.ui.modules.repos.extras.branches

import android.os.Bundle
import com.fastaccess.data.dao.BranchesModel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 06 Jul 2017, 9:06 PM
 */
interface BranchesMvp {

    interface View : BaseMvp.FAView {
        fun onNotifyAdapter(branches: ArrayList<BranchesModel>, page: Int)
        fun onBranchSelected(item: BranchesModel?)
        fun getLoadMore(): OnLoadMore<Boolean>
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<BranchesModel>, BaseMvp.PaginationListener<Boolean> {
        fun onFragmentCreated(bundle: Bundle)
    }

    interface BranchSelectionListener {
        fun onBranchSelected(branch: BranchesModel)
    }
}
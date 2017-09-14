package com.fastaccess.ui.modules.repos.projects.columns

import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Hashemsergani on 11.09.17.
 */

interface ProjectColumnMvp {
    interface View : BaseMvp.FAView {
        fun onNotifyAdapter(items: List<ProjectCardModel>?, page: Int)
        fun getLoadMore(): OnLoadMore<Long>
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<ProjectCardModel>, BaseMvp.PaginationListener<Long> {
        fun getCards(): ArrayList<ProjectCardModel>
    }
}
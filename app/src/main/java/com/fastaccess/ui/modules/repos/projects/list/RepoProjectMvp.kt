package com.fastaccess.ui.modules.repos.projects.list

import android.os.Bundle
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import github.RepoProjectsOpenQuery
import java.util.*

/**
 * Created by kosh on 09/09/2017.
 */
interface RepoProjectMvp {

    interface View : BaseMvp.FAView {
        fun onNotifyAdapter(items: List<RepoProjectsOpenQuery.Node>?, page: Int)
        fun getLoadMore(): OnLoadMore<IssueState>
        fun onChangeTotalCount(count: Int)
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<RepoProjectsOpenQuery.Node>,
            BaseMvp.PaginationListener<IssueState> {

        fun onFragmentCreate(bundle: Bundle?)

        fun getProjects(): ArrayList<RepoProjectsOpenQuery.Node>
    }
}
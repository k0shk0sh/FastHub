package com.fastaccess.ui.modules.repos.projects.list

import android.os.Bundle
import com.fastaccess.data.dao.ProjectsModel
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import java.util.*

/**
 * Created by kosh on 09/09/2017.
 */
interface RepoProjectMvp {

    interface View : BaseMvp.FAView {
        fun onNotifyAdapter(items: List<ProjectsModel>?, page: Int)
        fun getLoadMore(): OnLoadMore<IssueState>
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<ProjectsModel>,
            BaseMvp.PaginationListener<IssueState> {

        fun onFragmentCreate(bundle: Bundle?)

        fun getProjects(): ArrayList<ProjectsModel>
    }
}
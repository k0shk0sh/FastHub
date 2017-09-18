package com.fastaccess.ui.modules.repos.projects.details

import android.content.Intent
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Hashemsergani on 11.09.17.
 */
interface ProjectPagerMvp {

    interface View : BaseMvp.FAView, DeletePageListener {
        fun onInitPager(list: List<ProjectColumnModel>)
    }

    interface Presenter {
        fun onActivityCreated(intent: Intent?)

        fun onRetrieveColumns()

        fun getColumns(): ArrayList<ProjectColumnModel>
    }

    interface DeletePageListener {
        fun onDeletePage(model: ProjectColumnModel)
    }
}
package com.fastaccess.ui.modules.repos.projects.columns

import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.modules.repos.projects.crud.ProjectCurdDialogFragment
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Hashemsergani on 11.09.17.
 */

interface ProjectColumnMvp {
    interface View : BaseMvp.FAView, ProjectCurdDialogFragment.OnProjectEditedCallback {
        fun onNotifyAdapter(items: List<ProjectCardModel>?, page: Int)
        fun getLoadMore(): OnLoadMore<Long>
        fun deleteColumn()
        fun showBlockingProgress()
        fun hideBlockingProgress()
        fun isOwner(): Boolean
        fun onDeleteCard(position: Int)
        fun onEditCard(note: String?, position: Int)
        fun addCard(it: ProjectCardModel)
        fun updateCard(response: ProjectCardModel, position: Int)
        fun onRemoveCard(position: Int)
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<ProjectCardModel>, BaseMvp.PaginationListener<Long> {
        fun getCards(): ArrayList<ProjectCardModel>
        fun onEditOrDeleteColumn(text: String? = null, column: ProjectColumnModel)
        fun onDeleteCard(position: Int, card: ProjectCardModel)
        fun createCard(text: String, columnId: Long)
        fun editCard(text: String, card: ProjectCardModel, position: Int)
    }
}
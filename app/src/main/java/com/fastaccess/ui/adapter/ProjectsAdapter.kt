package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.ProjectsModel
import com.fastaccess.ui.adapter.viewholder.ProjectViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 09/09/2017.
 */
class ProjectsAdapter(data: ArrayList<ProjectsModel>) :
        BaseRecyclerAdapter<ProjectsModel, ProjectViewHolder, BaseViewHolder.OnItemClickListener<ProjectsModel>>(data) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder = ProjectViewHolder.newInstance(parent, this)

    override fun onBindView(holder: ProjectViewHolder?, position: Int) {
        holder?.bind(data[position])
    }
}
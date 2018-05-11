package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.ui.adapter.viewholder.ProjectViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import github.RepoProjectsOpenQuery

/**
 * Created by kosh on 09/09/2017.
 */
class ProjectsAdapter(data: ArrayList<RepoProjectsOpenQuery.Node>) :
        BaseRecyclerAdapter<RepoProjectsOpenQuery.Node, ProjectViewHolder, BaseViewHolder.OnItemClickListener<RepoProjectsOpenQuery.Node>>(data) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder = ProjectViewHolder.newInstance(parent, this)

    override fun onBindView(holder: ProjectViewHolder?, position: Int) {
        holder?.bind(data[position])
    }
}
package com.fastaccess.fasthub.commit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.model.CommitFilesModel
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.base.extensions.popMenu
import kotlinx.android.synthetic.main.commit_file_row_item.view.*

class CommitFileViewHolder(
    parent: ViewGroup
) : BaseViewHolder<CommitFilesModel>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.commit_file_row_item, parent, false)
) {

    override fun bind(item: CommitFilesModel) {
        itemView.apply {
            fileName.text = item.filename
            changes.text = "${item.changes ?: 0}"
            additions.text = "${item.additions ?: 0}"
            deletion.text = "${item.deletions ?: 0}"
            status.text = item.status?.capitalize() ?: ""

            menu.popMenu(R.menu.commit_file_menu, null,
                { itemId ->
                    //TODO
                })
        }
    }
}
package com.fastaccess.github.ui.adapter

import android.content.Context
import com.fastaccess.data.persistence.models.UserOrganizationNodesModel
import com.fastaccess.github.R
import com.fastaccess.github.platform.glide.GlideRequests
import com.jaychang.srv.kae.SimpleCell
import com.jaychang.srv.kae.SimpleViewHolder
import kotlinx.android.synthetic.main.icon_row_item.view.*

/**
 * Created by Kosh on 26.08.18.
 */
class ProfileOrganizationCell(private val node: UserOrganizationNodesModel,
                              private val glide: GlideRequests) : SimpleCell<UserOrganizationNodesModel>(node) {
    override fun getLayoutRes(): Int = R.layout.icon_row_item

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        glide.load(node.avatarUrl)
                .circleCrop()
                .into(holder.itemView.imageIcon)
    }
}
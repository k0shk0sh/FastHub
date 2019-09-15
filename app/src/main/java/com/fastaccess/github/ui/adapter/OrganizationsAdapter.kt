package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.persistence.models.OrganizationModel
import com.fastaccess.github.base.adapter.BasePagedAdapter
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.OrganizationViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class OrganizationsAdapter(private val onClick: (url: String) -> Unit) : BasePagedAdapter<OrganizationModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<OrganizationModel> = OrganizationViewHolder(parent).apply {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.url?.let(onClick)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrganizationModel?>() {
            override fun areItemsTheSame(oldItem: OrganizationModel, newItem: OrganizationModel): Boolean = oldItem.login == newItem.login
            override fun areContentsTheSame(oldItem: OrganizationModel, newItem: OrganizationModel): Boolean = oldItem == newItem
        }
    }
}
package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.fastaccess.data.persistence.models.ProfileGistModel
import com.fastaccess.github.ui.adapter.base.BasePagedAdapter
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.ProfileGistsViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class ProfileGistsAdapter : BasePagedAdapter<ProfileGistModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<ProfileGistModel> = ProfileGistsViewHolder(parent)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfileGistModel?>() {
            override fun areItemsTheSame(oldItem: ProfileGistModel, newItem: ProfileGistModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ProfileGistModel, newItem: ProfileGistModel): Boolean = oldItem == newItem
        }
    }
}
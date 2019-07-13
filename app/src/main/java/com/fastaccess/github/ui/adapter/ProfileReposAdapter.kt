package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.fastaccess.data.persistence.models.ProfileRepoModel
import com.fastaccess.github.ui.adapter.base.BasePagedAdapter
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.ReposProfileViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class ProfileReposAdapter : BasePagedAdapter<ProfileRepoModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<ProfileRepoModel> = ReposProfileViewHolder(parent)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfileRepoModel?>() {
            override fun areItemsTheSame(oldItem: ProfileRepoModel, newItem: ProfileRepoModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ProfileRepoModel, newItem: ProfileRepoModel): Boolean = oldItem == newItem
        }
    }
}
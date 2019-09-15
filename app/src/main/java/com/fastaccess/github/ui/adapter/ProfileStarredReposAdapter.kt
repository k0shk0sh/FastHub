package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.fastaccess.data.persistence.models.ProfileStarredRepoModel
import com.fastaccess.github.base.adapter.BasePagedAdapter
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.ReposStarredProfileViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class ProfileStarredReposAdapter : BasePagedAdapter<ProfileStarredRepoModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<ProfileStarredRepoModel> = ReposStarredProfileViewHolder(parent)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfileStarredRepoModel?>() {
            override fun areItemsTheSame(oldItem: ProfileStarredRepoModel, newItem: ProfileStarredRepoModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ProfileStarredRepoModel, newItem: ProfileStarredRepoModel): Boolean = oldItem == newItem
        }
    }
}
package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.github.ui.adapter.base.BasePagedAdapter
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.ProfileFeedsViewHolderr

/**
 * Created by Kosh on 12.10.18.
 */
class ProfileFeedsAdapter : BasePagedAdapter<FeedModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<FeedModel> = ProfileFeedsViewHolderr(parent)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeedModel?>() {
            override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean = oldItem == newItem
        }
    }
}
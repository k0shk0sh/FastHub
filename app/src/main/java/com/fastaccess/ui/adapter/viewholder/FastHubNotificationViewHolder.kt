package com.fastaccess.ui.adapter.viewholder

import android.view.View
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.model.FastHubNotification
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created: by Kosh on 02 Jun 2017, 1:27 PM
 */

open class FastHubNotificationViewHolder(itemView: View, adapter: BaseRecyclerAdapter<FastHubNotification,
        FastHubNotificationViewHolder, OnItemClickListener<FastHubNotification>>) : BaseViewHolder<FastHubNotification>(itemView, adapter) {

    @BindView(R.id.title) lateinit var title: FontTextView
    @BindView(R.id.date) lateinit var date: FontTextView
    @BindView(R.id.type) lateinit var type: FontTextView


    override fun bind(t: FastHubNotification) {
        title.text = t.title
        if (t.date != null) {
            date.text = ParseDateFormat.getTimeAgo(t.date)
        }
        type.text = t.type.name.replace("_", " ")
    }

}
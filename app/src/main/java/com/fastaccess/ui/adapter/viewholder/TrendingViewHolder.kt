package com.fastaccess.ui.adapter.viewholder

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingResponse
import com.fastaccess.provider.emoji.EmojiParser
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 02 Jun 2017, 1:27 PM
 */

open class TrendingViewHolder(itemView: View, adapter: BaseRecyclerAdapter<TrendingResponse,
        TrendingViewHolder, OnItemClickListener<TrendingResponse>>) : BaseViewHolder<TrendingResponse>(itemView, adapter) {

    val title by lazy { itemView.findViewById(R.id.title) as FontTextView }
    val description by lazy { itemView.findViewById(R.id.description) as FontTextView }
    val todayStars by lazy { itemView.findViewById(R.id.todayStars) as FontTextView }
    val stars by lazy { itemView.findViewById(R.id.stars) as FontTextView }
    val fork by lazy { itemView.findViewById(R.id.forks) as FontTextView }

    override fun bind(t: TrendingResponse) {
        title.text = t.title
        if (!t.description.isNullOrEmpty()) {
            val descriptionValue: String = EmojiParser.parseToUnicode(t.description)
            description.text = descriptionValue
            description.visibility = View.VISIBLE
        } else {
            description.visibility = View.GONE
        }
        todayStars.text = t.todayStars
        stars.text = t.stars
        fork.text = t.forks
    }

}
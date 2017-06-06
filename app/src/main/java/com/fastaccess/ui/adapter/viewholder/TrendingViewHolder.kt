package com.fastaccess.ui.adapter.viewholder

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.provider.colors.ColorsProvider
import com.fastaccess.provider.emoji.EmojiParser
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 02 Jun 2017, 1:27 PM
 */

open class TrendingViewHolder(itemView: View, adapter: BaseRecyclerAdapter<TrendingModel,
        TrendingViewHolder, OnItemClickListener<TrendingModel>>) : BaseViewHolder<TrendingModel>(itemView, adapter) {

    val title by lazy { itemView.findViewById(R.id.title) as FontTextView }
    val description by lazy { itemView.findViewById(R.id.description) as FontTextView }
    val todayStars by lazy { itemView.findViewById(R.id.todayStars) as FontTextView }
    val stars by lazy { itemView.findViewById(R.id.stars) as FontTextView }
    val fork by lazy { itemView.findViewById(R.id.forks) as FontTextView }
    val lang by lazy { itemView.findViewById(R.id.language) as FontTextView }

    override fun bind(t: TrendingModel) {
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
        lang.text = t.language
        if (!t.language.isNullOrEmpty()) {
            lang.tintDrawables(ColorsProvider.getColorAsColor(t.language!!, itemView.context))
            lang.setTextColor(ColorsProvider.getColorAsColor(t.language, itemView.context))
        }
        lang.visibility = if (t.language.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

}
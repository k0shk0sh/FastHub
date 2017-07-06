package com.fastaccess.ui.adapter.viewholder

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.provider.colors.ColorsProvider
import com.fastaccess.provider.emoji.EmojiParser
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.bindView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created: FontTextView by Kosh on 02 Jun 2017, 1:27 PM
 */

open class TrendingViewHolder(itemView: View, adapter: BaseRecyclerAdapter<TrendingModel,
        TrendingViewHolder, OnItemClickListener<TrendingModel>>) : BaseViewHolder<TrendingModel>(itemView, adapter) {

    val title: FontTextView by bindView(R.id.title)
    val description: FontTextView by bindView(R.id.description)
    val todayStars: FontTextView by bindView(R.id.todayStars)
    val stars: FontTextView by bindView(R.id.stars)
    val fork: FontTextView by bindView(R.id.forks)
    val lang: FontTextView by bindView(R.id.language)

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
            val color = ColorsProvider.getColorAsColor(t.language!!, itemView.context)
            lang.tintDrawables(color)
            lang.setTextColor(color)
        }
        todayStars.visibility = if (t.todayStars.isNullOrEmpty()) View.GONE else View.VISIBLE
        lang.visibility = if (t.language.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

}
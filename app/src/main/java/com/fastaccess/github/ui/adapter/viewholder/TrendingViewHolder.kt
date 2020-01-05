package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.TrendingModel
import com.fastaccess.github.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.trending_row_item.view.*

/**
 * Created by Kosh on 2018-11-17.
 */
class TrendingViewHolder(parent: ViewGroup) : BaseViewHolder<TrendingModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.trending_row_item, parent, false)) {

    override fun bind(item: TrendingModel) {
        itemView.apply {
            title.text = item.title
            description.isVisible = !item.description.isNullOrEmpty()
            description.text = item.description
            star.text = item.stars
            forks.text = item.forks
            todayStars.isVisible = !item.todayStars.isNullOrEmpty()
            todayStars.text = item.todayStars
            language.isVisible = !item.language.isNullOrEmpty()
            language.text = item.language
        }
    }

}
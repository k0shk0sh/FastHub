package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.ui.adapter.viewholder.TrendingViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 02 Jun 2017, 1:36 PM
 */

class TrendingAdapter(data: MutableList<TrendingModel>) : BaseRecyclerAdapter<TrendingModel,
        TrendingViewHolder, BaseViewHolder.OnItemClickListener<TrendingModel>>(data) {

    override fun viewHolder(parent: ViewGroup?, viewType: Int): TrendingViewHolder {
        return TrendingViewHolder(BaseViewHolder.getView(parent!!, R.layout.trending_row_item), this)
    }

    override fun onBindView(holder: TrendingViewHolder?, position: Int) {
        holder?.bind(getItem(position))
    }

}
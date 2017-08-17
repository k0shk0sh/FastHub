package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.adapter.viewholder.EmojiViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiAdapter(listener: BaseViewHolder.OnItemClickListener<Emoji>)
    : BaseRecyclerAdapter<Emoji, EmojiViewHolder, BaseViewHolder.OnItemClickListener<Emoji>>(listener) {
    override fun viewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        return EmojiViewHolder.newInstance(parent, this)
    }

    override fun onBindView(holder: EmojiViewHolder, position: Int) {
        holder.bind(data[position])
    }

}
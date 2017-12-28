package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import ru.noties.markwon.extension.emoji.loader.EmojiModel

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiViewHolder private constructor(view: View, adapter: BaseRecyclerAdapter<*, *, *>)
    : BaseViewHolder<EmojiModel>(view, adapter) {

    @BindView(R.id.emoji) lateinit var emojiTextView: FontTextView

    override fun bind(t: EmojiModel) {
        emojiTextView.text = t.unicode
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): EmojiViewHolder {
            return EmojiViewHolder(getView(parent, R.layout.emoji_row_item), adapter)
        }
    }
}
package com.fastaccess.ui.modules.editor.emoji

import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 17/08/2017.
 */
interface EmojiMvp {

    interface View : BaseMvp.FAView, BaseViewHolder.OnItemClickListener<Emoji> {
        fun clearAdapter()
        fun onAddEmoji(emoji: Emoji)
    }

    interface Presenter {
        fun onLoadEmoji()
    }

    interface EmojiCallback {
        fun onEmojiAdded(emoji: Emoji?)
    }
}
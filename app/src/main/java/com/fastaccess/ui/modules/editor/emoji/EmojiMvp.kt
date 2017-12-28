package com.fastaccess.ui.modules.editor.emoji

import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import ru.noties.markwon.extension.emoji.loader.EmojiModel

/**
 * Created by kosh on 17/08/2017.
 */
interface EmojiMvp {

    interface View : BaseMvp.FAView, BaseViewHolder.OnItemClickListener<EmojiModel> {
        fun clearAdapter()
        fun onAddEmoji(emoji: EmojiModel)
    }

    interface Presenter {
        fun onLoadEmoji()
    }

    interface EmojiCallback {
        fun onEmojiAdded(emoji: EmojiModel?)
    }
}
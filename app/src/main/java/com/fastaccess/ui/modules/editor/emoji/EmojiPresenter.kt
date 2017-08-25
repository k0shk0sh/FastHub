package com.fastaccess.ui.modules.editor.emoji

import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.provider.emoji.EmojiManager
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by kosh on 17/08/2017.
 */

class EmojiPresenter : BasePresenter<EmojiMvp.View>(), EmojiMvp.Presenter {
    override fun onLoadEmoji() {
        manageObservable(Observable.create<Emoji> { e ->
            val emojies = EmojiManager.getAll()
            emojies?.let {
                it.onEach {
                    if (!e.isDisposed) {
                        e.onNext(it)
                    }
                }
            }
            e.onComplete()
        }
                .doOnSubscribe { sendToView { it.clearAdapter() } }
                .doOnNext { emoji -> sendToView { it.onAddEmoji(emoji) } })
    }

}
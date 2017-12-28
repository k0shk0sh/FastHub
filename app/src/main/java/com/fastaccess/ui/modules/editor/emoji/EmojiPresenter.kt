package com.fastaccess.ui.modules.editor.emoji


import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable
import ru.noties.markwon.extension.emoji.loader.EmojiManager
import ru.noties.markwon.extension.emoji.loader.EmojiModel

/**
 * Created by kosh on 17/08/2017.
 */

class EmojiPresenter : BasePresenter<EmojiMvp.View>(), EmojiMvp.Presenter {
    override fun onLoadEmoji() {
        manageObservable(Observable.create<EmojiModel> { e ->
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
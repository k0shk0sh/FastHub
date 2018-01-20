package com.fastaccess.ui.modules.repos.git

import android.content.Intent
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.modules.editor.emoji.EmojiMvp
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp
import com.fastaccess.ui.widgets.markdown.MarkDownLayout

/**
 * Created by kosh on 29/08/2017.
 */
interface EditRepoFileMvp {

    interface View : BaseMvp.FAView, EditorLinkImageMvp.EditorLinkCallback,
            MarkDownLayout.MarkdownListener, EmojiMvp.EmojiCallback {

        fun onSetText(content: String?)
        fun onSetTextError(isEmpty: Boolean)
        fun onSetDescriptionError(isEmpty: Boolean)
        fun onSetFilenameError(isEmpty: Boolean)
        fun onSuccessfullyCommitted()
    }

    interface Presenter {
        fun onInit(intent: Intent?)
        fun onSubmit(text: String?, filename: String?, description: String?)
    }
}
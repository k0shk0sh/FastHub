package com.fastaccess.ui.modules.gists.create.dialog

import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.modules.editor.emoji.EmojiMvp
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp
import com.fastaccess.ui.widgets.markdown.MarkDownLayout

/**
 * Created by kosh on 14/08/2017.
 */
interface AddGistMvp {

    interface View : BaseMvp.FAView, EditorLinkImageMvp.EditorLinkCallback, MarkDownLayout.MarkdownListener, EmojiMvp.EmojiCallback
    interface Presenter
    interface AddGistFileListener {
        fun onFileAdded(file: FilesListModel, position: Int? = -1)
    }
}
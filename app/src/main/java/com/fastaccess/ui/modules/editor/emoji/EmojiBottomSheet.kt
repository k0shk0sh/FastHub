package com.fastaccess.ui.modules.editor.emoji

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import butterknife.BindView
import butterknife.OnTextChanged
import com.fastaccess.R
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.adapter.EmojiAdapter
import com.fastaccess.ui.base.BaseMvpBottomSheetDialogFragment
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiBottomSheet : BaseMvpBottomSheetDialogFragment<EmojiMvp.View, EmojiPresenter>(), EmojiMvp.View {

    @BindView(R.id.recycler) lateinit var recycler: DynamicRecyclerView
    @BindView(R.id.fastScroller) lateinit var fastScroller: RecyclerViewFastScroller

    val adapter: EmojiAdapter by lazy { EmojiAdapter(this) }

    var emojiCallback: EmojiMvp.EmojiCallback? = null


    @OnTextChanged(value = [(R.id.editText)], callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChange(text: Editable) {
        adapter.filter.filter(text)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        emojiCallback = when {
            parentFragment is EmojiMvp.EmojiCallback -> parentFragment as EmojiMvp.EmojiCallback
            context is EmojiMvp.EmojiCallback -> context
            else -> throw IllegalArgumentException("${context.javaClass.simpleName} must implement EmojiMvp.EmojiCallback")
        }
    }

    override fun onDetach() {
        emojiCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.emoji_popup_layout

    override fun providePresenter(): EmojiPresenter = EmojiPresenter()

    override fun clearAdapter() {
        adapter.clear()
    }

    override fun onAddEmoji(emoji: Emoji) {
        adapter.addItem(emoji)
    }

    override fun onItemClick(position: Int, v: View?, item: Emoji) {
        emojiCallback?.onEmojiAdded(item)
        dismiss()
    }

    override fun onItemLongClick(position: Int, v: View?, item: Emoji?) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter = adapter
        fastScroller.attachRecyclerView(recycler)
        presenter.onLoadEmoji()
        val gridManager = recycler.layoutManager as GridManager
        gridManager.iconSize = resources.getDimensionPixelSize(R.dimen.header_icon_zie)
    }
}
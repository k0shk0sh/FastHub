package com.fastaccess.ui.modules.repos.projects.crud

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.editor.emoji.EmojiMvp
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageMvp
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText

/**
 * Created by Hashemsergani on 15.09.17.
 */

class ProjectCurdDialogFragment : BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(),
        EditorLinkImageMvp.EditorLinkCallback, MarkDownLayout.MarkdownListener, EmojiMvp.EmojiCallback {

    @BindView(R.id.editText) lateinit var editText: MarkdownEditText
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.markDownLayout) lateinit var markDownLayout: MarkDownLayout

    private var onProjectEditedCallback: OnProjectEditedCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onProjectEditedCallback = when {
            parentFragment is OnProjectEditedCallback -> parentFragment as OnProjectEditedCallback
            context is OnProjectEditedCallback -> context
            else -> throw NullPointerException("${context::class.java.simpleName} most implement OnProjectEditedCallback")
        }
    }

    override fun onDetach() {
        onProjectEditedCallback = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = R.layout.edit_project_column_note_layout

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        markDownLayout.markdownListener = this
        toolbar.inflateMenu(R.menu.done_menu)
        toolbar.menu.findItem(R.id.submit)?.setIcon(R.drawable.ic_done)
        toolbar.setNavigationIcon(R.drawable.ic_clear)
        toolbar.setNavigationOnClickListener { dismiss() }
        val position: Int = arguments!!.getInt(BundleConstant.ID, -1)
        val isCard: Boolean = arguments!!.getBoolean(BundleConstant.EXTRA)
        if (savedInstanceState == null) {
            editText.setText(arguments?.getString(BundleConstant.ITEM) ?: "")
        }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.submit) {
                val isEmpty = editText.text.isNullOrBlank()
                editText.error = if (isEmpty) getString(R.string.required_field) else null
                if (!isEmpty) {
                    onProjectEditedCallback?.onCreatedOrEdited(InputHelper.toString(editText), isCard, position)
                    dismiss()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun getEditText(): EditText = editText

    override fun fragmentManager(): FragmentManager = childFragmentManager

    override fun getSavedText(): CharSequence? = editText.savedText

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markDownLayout.onAppendLink(title, link, isLink)
    }

    @SuppressLint("SetTextI18n")
    override fun onEmojiAdded(emoji: Emoji?) {
        markDownLayout.onEmojiAdded(emoji)
    }

    companion object {
        val TAG = ProjectCurdDialogFragment::class.java.simpleName!!

        fun newInstance(text: String? = null, isCard: Boolean = false, position: Int = -1): ProjectCurdDialogFragment {
            val fragment = ProjectCurdDialogFragment()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, text)
                    .put(BundleConstant.EXTRA, isCard)
                    .put(BundleConstant.ID, position)
                    .end()
            return fragment
        }
    }

    interface OnProjectEditedCallback {
        fun onCreatedOrEdited(text: String, isCard: Boolean, position: Int)
    }

}
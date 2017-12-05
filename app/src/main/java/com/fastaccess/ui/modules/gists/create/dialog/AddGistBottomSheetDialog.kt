package com.fastaccess.ui.modules.gists.create.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TextInputLayout
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.FilesListModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.modules.gists.create.dialog.AddGistMvp.AddGistFileListener
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText

/**
 * Created by kosh on 14/08/2017.
 */
class AddGistBottomSheetDialog : BaseDialogFragment<AddGistMvp.View, AddGistPresenter>(), AddGistMvp.View {

    @BindView(R.id.editText) lateinit var editText: MarkdownEditText
    @BindView(R.id.description) lateinit var description: TextInputLayout
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.markDownLayout) lateinit var markDownLayout: MarkDownLayout

    private var addFileListener: AddGistFileListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addFileListener = when {
            parentFragment is AddGistFileListener -> parentFragment as AddGistFileListener
            context is AddGistFileListener -> context
            else -> throw NullPointerException("${context::class.java.simpleName} most implement AddGistFileListener")
        }
    }

    override fun onDetach() {
        addFileListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun providePresenter(): AddGistPresenter = AddGistPresenter()

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markDownLayout.onAppendLink(title, link, isLink)
    }

    override fun fragmentLayout(): Int = R.layout.add_gist_file_layout

    override fun getEditText(): EditText = editText

    override fun fragmentManager(): FragmentManager = childFragmentManager

    override fun getSavedText(): CharSequence? = editText.savedText

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        markDownLayout.markdownListener = this
        var file = arguments?.getParcelable<FilesListModel>(BundleConstant.ITEM)
        val position = arguments?.getInt(BundleConstant.ID)
        if (position != null) {
            toolbar.title = if (position > 0) getString(R.string.edit_gist) else getString(R.string.create_gist)
        } else {
            toolbar.title = getString(R.string.create_gist)
        }
        editText.setOnFocusChangeListener { _, focused -> markDownLayout.visibility = if (focused) View.VISIBLE else View.GONE }
        toolbar.inflateMenu(R.menu.done_menu)
        toolbar.menu.findItem(R.id.submit)?.setIcon(R.drawable.ic_done)
        toolbar.setNavigationIcon(R.drawable.ic_clear)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.submit) {
                if (file == null) {
                    file = FilesListModel()
                }
                file?.let {
                    it.content = getSavedText().toString()
                    it.filename = InputHelper.toString(description)
                    it.type = MimeTypeMap.getFileExtensionFromUrl(file!!.filename)
                    it.size = it.content?.length?.toLong()
                    ViewHelper.hideKeyboard(editText)
                    addFileListener?.onFileAdded(it, position)
                }
            }
            dismiss()
            return@setOnMenuItemClickListener true
        }
        file?.let {
            description.editText?.setText(it.filename)
            editText.setText(it.content)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onEmojiAdded(emoji: Emoji?) {
        markDownLayout.onEmojiAdded(emoji)
    }

    companion object {
        val TAG = AddGistBottomSheetDialog::class.java.simpleName!!

        fun newInstance(file: FilesListModel?, position: Int = -1): AddGistBottomSheetDialog {
            val fragment = AddGistBottomSheetDialog()
            file?.let {
                fragment.arguments = Bundler.start()
                        .put(BundleConstant.ITEM, file as Parcelable)
                        .put(BundleConstant.ID, position)
                        .end()
            }
            return fragment
        }
    }
}
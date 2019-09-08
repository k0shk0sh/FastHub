package com.fastaccess.github.ui.modules.editor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.extensions.hideKeyboard
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.show
import com.fastaccess.github.platform.mentions.MentionsPresenter
import com.fastaccess.github.ui.modules.editor.dialog.CreateLinkDialogFragment
import com.fastaccess.github.ui.widget.dialog.IconDialogFragment
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.asString
import com.fastaccess.github.utils.extensions.showKeyboard
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.widget.MarkdownLayout
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.CharPolicy
import io.noties.markwon.Markwon
import io.noties.markwon.recycler.MarkwonAdapter
import kotlinx.android.synthetic.main.editor_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-20.
 */
class EditorFragment : BaseFragment(), IconDialogFragment.IconDialogClickListener,
                       MarkdownLayout.MarkdownLayoutCallback,
                       CreateLinkDialogFragment.OnLinkSelected {

    @Inject lateinit var markwon: Markwon
    @Inject lateinit var preference: FastHubSharedPreference
    @Inject lateinit var mentionsPresenter: MentionsPresenter
    @Inject lateinit var markwonAdapterBuilder: MarkwonAdapter.Builder

    override fun viewModel(): BaseViewModel? = null
    override fun layoutRes(): Int = R.layout.editor_fragment_layout

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {

        if (savedInstanceState == null) {
            editText.setText(arguments?.getString(EXTRA) ?: "")
        }
        editText.showKeyboard()
        editText.setSelection(editText.asString().length)
        setupToolbar(R.string.markdown, R.menu.submit_menu) { item ->
            activity?.hideKeyboard()
            val intent = Intent().apply {
                val bundle = arguments  ?: bundleOf()
                bundle.putString(EXTRA, editText.asString())
                putExtras(bundle)
            }
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
        mentionsPresenter.isMatchParent = true
        setToolbarNavigationIcon(R.drawable.ic_clear)
        markdownLayout.layoutCallback = this
        markdownLayout.init()
        initEditText()
    }

    override fun onDestroyView() {
        mentionsPresenter.onDispose()
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        if (editText.asString().isNotBlank()) {
            IconDialogFragment.show(
                childFragmentManager, R.drawable.ic_info,
                getString(R.string.close), getString(R.string.confirm_message),
                getString(R.string.close), getString(R.string.cancel)
            )
            return false
        }
        return true
    }

    override fun onClick(positive: Boolean) {
        positive.isTrue { activity?.finish() }
    }

    override fun provideEditText(): EditText = editText
    override fun provideReview(isReview: Boolean) {
        if (isReview) {
            preview.isVisible = true
            markwon.setMarkdown(preview, editText.asString())
        } else {
            preview.setText("")
            preview.isVisible = false
        }
    }

    override fun openLinkDialog(isImage: Boolean) = CreateLinkDialogFragment.newInstance(isImage).show(childFragmentManager)

    override fun onLinkSelected(
        title: String,
        link: String,
        isImage: Boolean
    ) = markdownLayout.onLinkSelected(title, link, isImage)

    private fun initEditText() {
        Autocomplete.on<String>(editText)
            .with(CharPolicy('@'))
            .with(mentionsPresenter)
            .with(requireContext().getDrawableCompat(R.drawable.popup_window_background))
            .with(object : AutocompleteCallback<String?> {
                override fun onPopupItemClicked(
                    editable: Editable?,
                    item: String?
                ): Boolean = MarkdownProvider.replaceMention(editable, item)

                override fun onPopupVisibilityChanged(shown: Boolean) {}
            })
            .build()
    }

    companion object {
        const val TAG = "EditorFragment"
        fun newInstance(bundle: Bundle?) = EditorFragment().apply {
            arguments = bundle
        }
    }
}
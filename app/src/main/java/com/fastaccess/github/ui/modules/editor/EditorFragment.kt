package com.fastaccess.github.ui.modules.editor

import android.os.Bundle
import android.text.Editable
import android.view.View
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.platform.mentions.MentionsPresenter
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
class EditorFragment : BaseFragment() {

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
        setupToolbar(R.string.markdown, R.menu.submit_menu, { item ->

        })

        mentionsPresenter.isMatchParent = true
        setToolbarNavigationIcon(R.drawable.ic_clear)
        markdownLayout.init(editText)
        initEditText()
    }

    override fun onDestroyView() {
        mentionsPresenter.onDispose()
        super.onDestroyView()
    }

    private fun initEditText() {
        Autocomplete.on<String>(editText)
            .with(CharPolicy('@'))
            .with(mentionsPresenter)
            .with(requireContext().getDrawableCompat(R.drawable.popup_window_background))
            .with(object : AutocompleteCallback<String?> {
                override fun onPopupItemClicked(
                    editable: Editable?,
                    item: String?
                ): Boolean {
                    val range = CharPolicy.getQueryRange(editable) ?: return false
                    val start = range[0]
                    val end = range[1]
                    editable?.replace(start, end, "$item ")
                    return true
                }

                override fun onPopupVisibilityChanged(shown: Boolean) {}
            })
            .build()
    }

    companion object {
        fun newInstance(bundle: Bundle?) = EditorFragment().apply {
            arguments = bundle
        }
    }
}
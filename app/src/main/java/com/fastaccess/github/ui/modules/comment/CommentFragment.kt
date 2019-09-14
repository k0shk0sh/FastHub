package com.fastaccess.github.ui.modules.comment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.os.bundleOf
import com.fastaccess.github.R
import com.fastaccess.github.base.utils.*
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.extensions.routeForResult
import com.fastaccess.github.platform.mentions.MentionsPresenter
import com.fastaccess.markdown.MarkdownProvider
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.CharPolicy
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.comment_box_layout.*
import kotlinx.android.synthetic.main.comment_fragment_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 2019-08-18.
 */
class CommentFragment : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var markwon: Markwon
    @Inject lateinit var mentionsPresenter: MentionsPresenter

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = null
    override fun layoutRes(): Int = R.layout.comment_fragment_layout

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        setToolbarNavigationIcon(R.drawable.ic_clear)
        setupToolbar(R.string.comment)
        arguments?.let { bundle ->
            userIcon.loadAvatar(bundle.getString(EXTRA_FIVE))
            name.text = bundle.getString(EXTRA_FOUR)
            val comment = bundle.getString(EXTRA_THREE)

            preview.post {
                // description.setMovementMethod(LinkMovementMethod.getInstance()) TODO
                preview.setSpannableFactory(NoCopySpannableFactory.getInstance())
                markwon.setMarkdown(
                    preview, if (!comment.isNullOrEmpty()) comment else "**${getString(R.string.no_description_provided)}**"
                )
            }
        }
        Autocomplete.on<String>(commentText)
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
        sendComment.setOnClickListener {
            val comment = commentText.text?.toString()
            if (!comment.isNullOrEmpty()) {
                requireActivity().let {
                    it.setResult(Activity.RESULT_OK, Intent().apply { putExtra(EXTRA, comment) })
                    it.finish()
                }
            }
        }
        toggleFullScreen.setOnClickListener {
            routeForResult(EDITOR_DEEP_LINK, COMMENT_REQUEST_CODE, bundleOf(EXTRA to commentText.text?.toString()))
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                COMMENT_REQUEST_CODE -> {
                    commentText.setText(data?.getStringExtra(EXTRA))
                }
                else -> Timber.e("nothing yet for requestCode($requestCode)")
            }
        }
    }

    companion object {
        private const val COMMENT_REQUEST_CODE = 1000

        fun newInstance(bundle: Bundle) = CommentFragment().apply { arguments = bundle }
    }
}
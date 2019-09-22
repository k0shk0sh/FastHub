package com.fastaccess.fasthub.reviews

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.fasthub.reviews.adapter.ReviewsAdapter
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.extensions.theme
import com.fastaccess.github.base.utils.*
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.editor.comment.CommentActivity
import com.fastaccess.github.editor.presenter.MentionsPresenter
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.routeForResult
import com.fastaccess.markdown.MarkdownProvider
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.CharPolicy
import io.noties.markwon.Markwon
import javax.inject.Inject

class ReviewsFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var markwon: Markwon
    @Inject lateinit var preference: FastHubSharedPreference
    @Inject lateinit var mentionsPresenter: MentionsPresenter


    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ReviewsViewModel::class.java) }
    private val login by lazy { arguments?.getString(EXTRA) ?: throw NullPointerException("no login") }
    private val repo by lazy { arguments?.getString(EXTRA_TWO) ?: throw NullPointerException("no repo") }
    private val number by lazy { arguments?.getInt(EXTRA_THREE, 0) ?: throw NullPointerException("no number") }
    private val id by lazy { arguments?.getString(EXTRA_FOUR) }
    private val adapter by lazy {
        ReviewsAdapter(markwon, preference.theme, onCommentClicked(), onDeleteComment(), onEditCommentClicked())
    }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.reviews_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        val reviewId = id
        setupToolbar(R.string.reviews)
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                if (reviewId.isNullOrEmpty()) {
                    viewModel.load(login, repo, number, true)
                } else {
                    viewModel.load(reviewId, true)
                }
            } else {
                swipeRefresh.isRefreshing = false
            }
        }

        if (savedInstanceState == null || viewModel.timeline.value == null) {
            if (reviewId.isNullOrEmpty()) {
                viewModel.load(login, repo, number, true)
            } else {
                viewModel.load(reviewId, true)
            }
        }

        recyclerView.addOnLoadMore {
            isConnected().isTrue {
                if (reviewId.isNullOrEmpty()) {
                    viewModel.load(login, repo, number)
                } else {
                    viewModel.load(reviewId)
                }
            }
        }
        setupEditText()
        observeChanges()
    }

    private fun onCommentClicked(): (position: Int, comment: CommentModel) -> Unit = { position, comment ->
        CommentActivity.startActivity(
            this, COMMENT_REQUEST_CODE, comment.body ?: "",
            comment.author?.login ?: comment.author?.name, comment.author?.avatarUrl
        )
    }

    private fun onEditCommentClicked(): (position: Int, comment: TimelineModel) -> Unit = { position, comment ->
        val databaseId = if (comment.review != null) comment.review?.databaseId else comment.comment?.databaseId
        val body = if (comment.review != null) comment.review?.body else comment.comment?.body
        routeForResult(
            EDITOR_DEEP_LINK, EDIT_COMMENT_REQUEST_CODE, bundleOf(
                EXTRA to body,
                EXTRA_TWO to databaseId
            )
        )
    }

    private fun onDeleteComment(): (position: Int, comment: CommentModel) -> Unit = { position, comment ->
        viewModel.deleteComment(login, repo, comment.databaseId?.toLong() ?: 0)
    }

    private fun observeChanges() {
        viewModel.timeline.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    private fun setupEditText() {
        val commentText = view?.findViewById<EditText>(R.id.commentText) ?: return
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
        view?.findViewById<View?>(R.id.sendComment)?.setOnClickListener {
            val comment = commentText.text?.toString()
            if (!comment.isNullOrEmpty()) {
                viewModel.createComment(login, repo, number, comment)
            }
        }
        view?.findViewById<View?>(R.id.toggleFullScreen)?.setOnClickListener {
            routeForResult(EDITOR_DEEP_LINK, COMMENT_REQUEST_CODE, bundleOf(EXTRA to commentText.text?.toString()))
        }
    }

    companion object {
        const val COMMENT_REQUEST_CODE = 1001
        const val EDIT_COMMENT_REQUEST_CODE = 1003

        fun newInstance(
            login: String,
            repo: String,
            number: Int,
            id: String? = null
        ) = ReviewsFragment().apply {
            arguments = bundleOf(
                EXTRA to login,
                EXTRA_TWO to repo,
                EXTRA_THREE to number,
                EXTRA_FOUR to id
            )
        }
    }
}
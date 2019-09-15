package com.fastaccess.fasthub.commit.view.comment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.fasthub.commit.R
import com.fastaccess.fasthub.commit.adapter.CommitCommentsAdapter
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.extensions.hideKeyboard
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.extensions.theme
import com.fastaccess.github.base.utils.EDITOR_DEEP_LINK
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
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
import timber.log.Timber
import javax.inject.Inject

class CommitCommentsFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var markwon: Markwon
    @Inject lateinit var preference: FastHubSharedPreference
    @Inject lateinit var mentionsPresenter: MentionsPresenter

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(CommitCommentsViewModel::class.java) }
    private val adapter by lazy {
        CommitCommentsAdapter(
            markwon, preference.theme,
            onCommentClicked(), onDeleteComment(), onEditCommentClicked()
        )
    }

    private val sha by lazy { arguments?.getString(EXTRA) ?: throw NullPointerException("sha is null") }
    private val login by lazy { arguments?.getString(EXTRA_TWO) ?: throw NullPointerException("login is null") }
    private val repo by lazy { arguments?.getString(EXTRA_THREE) ?: throw NullPointerException("repo is null") }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadData(sha, login, repo, true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        setupEditText()
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadData(sha, login, repo) } }
        listenToChanges()
        if (savedInstanceState == null) {
            viewModel.loadData(sha, login, repo, true)
        }
    }

    override fun onDestroyView() {
        mentionsPresenter.onDispose()
        super.onDestroyView()
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
                    parentFragment?.view?.findViewById<EditText?>(R.id.commentText)?.let { commentText ->
                        commentText.setText(data?.getStringExtra(EXTRA))
                        parentFragment?.view?.findViewById<View?>(R.id.sendComment)?.callOnClick()
                    }
                }
                EDIT_COMMENT_REQUEST_CODE -> {
                    val comment = data?.getStringExtra(EXTRA)
                    val commentId = data?.getIntExtra(EXTRA_TWO, 0)
                    viewModel.editComment(login, repo, comment ?: "", commentId ?: 0)
                }
                else -> Timber.e("nothing yet for requestCode($requestCode)")
            }
        }
    }

    private fun setupEditText() {
        val commentText = parentFragment?.view?.findViewById<EditText>(R.id.commentText) ?: return
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
        parentFragment?.view?.findViewById<View?>(R.id.sendComment)?.setOnClickListener {
            val comment = commentText.text?.toString()
            if (!comment.isNullOrEmpty()) {
                viewModel.addComment(login, repo, sha, comment)
            }
        }
        parentFragment?.view?.findViewById<View?>(R.id.toggleFullScreen)?.setOnClickListener {
            routeForResult(EDITOR_DEEP_LINK, COMMENT_REQUEST_CODE, bundleOf(EXTRA to commentText.text?.toString()))
        }
    }

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }

        viewModel.progress.observeNotNull(this) {
            swipeRefresh.isRefreshing = it == true
        }

        viewModel.counter.observeNotNull(this) {
            postCount(FragmentType.COMMENTS, it)
        }

        viewModel.commentAddedLiveData.observeNotNull(this) {
            parentFragment?.view?.findViewById<EditText?>(R.id.commentText)?.let { commentText ->
                commentText.setText("")
                commentText.hideKeyboard()
            }
            view?.let { view -> showSnackBar(view, R.string.comments_added_successfully) }
        }

        viewModel.commentProgress.observeNotNull(this) {
            parentFragment?.view?.findViewById<View?>(R.id.commentProgress)?.isVisible = it
            parentFragment?.view?.findViewById<View?>(R.id.sendComment)?.isVisible = !it
        }
    }

    private fun onCommentClicked(): (position: Int, comment: CommentModel) -> Unit = { position, comment ->
        CommentActivity.startActivity(
            this, COMMENT_REQUEST_CODE, comment.body ?: "",
            comment.author?.login ?: comment.author?.name, comment.author?.avatarUrl
        )
    }

    private fun onEditCommentClicked(): (position: Int, comment: CommentModel) -> Unit = { position, comment ->
        routeForResult(
            EDITOR_DEEP_LINK, EDIT_COMMENT_REQUEST_CODE, bundleOf(
                EXTRA to comment.body,
                EXTRA_TWO to comment.databaseId
            )
        )
    }

    private fun onDeleteComment(): (position: Int, comment: CommentModel) -> Unit = { position, comment ->
        viewModel.deleteComment(login, repo, comment.databaseId?.toLong() ?: 0)
    }

    companion object {
        const val COMMENT_REQUEST_CODE = 1001
        const val EDIT_COMMENT_REQUEST_CODE = 1003

        fun newInstance(
            sha: String,
            login: String,
            repo: String
        ) = CommitCommentsFragment().apply {
            arguments = bundleOf(
                EXTRA to sha,
                EXTRA_TWO to login,
                EXTRA_THREE to repo
            )
        }
    }
}
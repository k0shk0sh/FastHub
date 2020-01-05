package com.fastaccess.github.ui.modules.pr.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.model.TimelineType
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.persistence.models.PullRequestModel
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.fasthub.reviews.PullRequestReviewsActivity
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.hideKeyboard
import com.fastaccess.github.base.extensions.theme
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.route
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.IssueTimelineAdapter
import com.fastaccess.github.ui.modules.issuesprs.BaseIssuePrTimelineFragment
import com.fastaccess.github.ui.modules.pr.fragment.viewmodel.PullRequestTimelineViewModel
import com.fastaccess.github.ui.modules.quickmsg.QuickMessageBottomSheetDialog
import com.fastaccess.github.ui.modules.quickmsg.QuickMessageBottomSheetDialog.QuickMessageCallback
import com.fastaccess.markdown.widget.SpannableBuilder
import github.type.CommentAuthorAssociation
import github.type.LockReason
import github.type.PullRequestState
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.pr_header_row_item.*
import kotlinx.android.synthetic.main.pr_view_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 28.01.19.
 */
class PullRequestFragment : BaseIssuePrTimelineFragment(), QuickMessageCallback {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var markwon: Markwon
    @Inject lateinit var preference: FastHubSharedPreference

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(PullRequestTimelineViewModel::class.java) }

    override val adapter by lazy {
        IssueTimelineAdapter(
            markwon, preference.theme, onCommentClicked(),
            onDeleteCommentClicked(),
            onEditCommentClicked(),
            { model -> route(PullRequestReviewsActivity.getUrl(login, repo, number)) }
        )
    }

    override fun layoutRes(): Int = R.layout.pr_fragment_layout
    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun isPr(): Boolean = true
    override fun lockIssuePr(lockReason: LockReason?, isLock: Boolean) = viewModel.lockUnlockIssue(login, repo, number, lockReason, isLock)
    override fun onMilestoneAdd(timeline: TimelineModel) = viewModel.addTimeline(timeline)
    override fun reload(refresh: Boolean) = viewModel.loadData(login, repo, number, refresh)
    override fun sendComment(comment: String) = viewModel.createComment(login, repo, number, comment)

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentCreatedWithUser(view, savedInstanceState)
        observeChanges()
    }

    override fun onDestroyView() {
        mentionsPresenter.onDispose()
        super.onDestroyView()
    }

    override fun editIssuerPr(
        title: String?,
        description: String?
    ) = viewModel.editIssue(login, repo, number, title, description)

    override fun closeOpenIssuePr() = viewModel.closeOpenIssue(login, repo, number)

    override fun onMessageEntered(msg: String, bundle: Bundle?) {
        val commentId = bundle?.getLong(EXTRA_TWO) ?: return
        viewModel.deleteComment(login, repo, commentId, TimelineType.REVIEW_BODY, number, msg)
    }

    private fun observeChanges() {
        viewModel.getPullRequest(login, repo, number).observeNotNull(this) {
            initIssue(it.first, it.second)
        }
        viewModel.timeline.observeNotNull(this) { timeline ->
            adapter.submitList(timeline)
        }
        viewModel.userNamesLiveData.observeNotNull(this) {
            mentionsPresenter.setUsers(it)
        }
        viewModel.commentProgress.observeNotNull(this) {
            view?.findViewById<View?>(R.id.commentProgress)?.isVisible = it
            view?.findViewById<View?>(R.id.sendComment)?.isVisible = !it
            if (!it) {
                view?.findViewById<EditText?>(R.id.commentText)?.let { commentText ->
                    commentText.setText("")
                    commentText.hideKeyboard()
                }
                recyclerView.scrollToPosition(adapter.itemCount)
            }
        }
        viewModel.forceAdapterUpdate.observeNotNull(this) {
            it.isTrue { adapter.notifyDataSetChanged() }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun initIssue(
        model: PullRequestModel,
        me: LoginModel?
    ) {
        view?.findViewById<View>(R.id.commentLayout)?.isVisible = model.locked == false
        issueHeaderWrapper.isVisible = true
        title.text = model.title

        opener.text = if (model.merged == true) {
            SpannableBuilder.builder()
                .bold(model.mergedBy?.login)
                .space()
                .append(getString(R.string.merged).toLowerCase())
                .space()
                .bold(model.headRefName)
                .space()
                .append(getString(R.string.to))
                .space()
                .bold(model.baseRefName)
                .space()
                .append(model.mergedAt?.timeAgo())
        } else {
            SpannableBuilder.builder()
                .bold(model.author?.login)
                .space()
                .append(getString(R.string.want_to_merge))
                .space()
                .bold(model.headRefName)
                .space()
                .append(getString(R.string.to))
                .space()
                .bold(model.baseRefName)
                .space()
                .append(model.createdAt?.timeAgo())
        }

        userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url ?: "")
        author.text = model.author?.login
        association.text = if (CommentAuthorAssociation.NONE.rawValue() == model.authorAssociation) {
            model.updatedAt?.timeAgo()
        } else {
            "${model.authorAssociation?.toLowerCase()?.replace("_", "")} ${model.updatedAt?.timeAgo()}"
        }

        description.post {
            val bodyMd = model.body
//            description.setMovementMethod(LinkMovementMethod.getInstance())
            description.setSpannableFactory(NoCopySpannableFactory.getInstance())
            markwon.setMarkdown(
                description, if (!bodyMd.isNullOrEmpty()) bodyMd else "**${getString(R.string.no_description_provided)}**"
            )
        }

        state.text = model.state?.toLowerCase()
        state.setChipBackgroundColorResource(
            when {
                PullRequestState.OPEN.rawValue().equals(model.state, true) -> R.color.material_green_500
                PullRequestState.CLOSED.rawValue().equals(model.state, true) -> R.color.material_red_500
                else -> R.color.material_blue_700
            }
        )

        adaptiveEmoticon.init(requireNotNull(model.id), model.reactionGroups) {
            adaptiveEmoticon.initReactions(model.reactionGroups)
        }
        val isAuthor = model.viewerCanUpdate == true
        initLabels(model.labels)
        initAssignees(model.assignees)
        initMilestone(model.milestone)
        initToolbarMenu(
            isAuthor,
            model.viewerCanUpdate == true,
            model.viewerDidAuthor,
            model.locked,
            model.merged == true,
            model.state
        )
        menuClick(model.url, model.labels, model.assignees, model.title, model.body, isAuthor)
        initDashboard(model)
        recyclerView.removeEmptyView()
        reviewFab.show()
    }

    private fun initDashboard(model: PullRequestModel) {
        commits.text = getString(R.string.commits_with_count, model.dashboard?.commits ?: 0)
        files.text = getString(R.string.files_with_count, model.dashboard?.changedFiles ?: 0)
        addition.text = getString(R.string.addition_with_count, model.dashboard?.additions ?: 0)
        deletion.text = getString(R.string.deletion_with_count, model.dashboard?.deletions ?: 0)
        approved.text = getString(R.string.approved_with_count, model.dashboard?.approvedReviews ?: 0)
        commented.text = getString(R.string.commented_with_count, model.dashboard?.commentedReviews ?: 0)
        changes.text = getString(R.string.changes_with_count, model.dashboard?.changeRequestedReviews ?: 0)
        commitsCard.setOnClickListener {
            it.context.route("${model.url}/commits")
        }
        reviewsCard.setOnClickListener {
            addDisposal(
                viewModel.hasCommentableReviews()
                    .subscribe { if (it) route(PullRequestReviewsActivity.getUrl(login, repo, number)) else Timber.e("no reviews") }
            )
        }
    }

    override fun deleteComment(login: String, repo: String, commentId: Long, type: TimelineType) {
        if (type == TimelineType.REVIEW_BODY) {
            QuickMessageBottomSheetDialog.show(
                childFragmentManager, bundleOf(
                    EXTRA to getString(R.string.dismiss_review),
                    EXTRA_TWO to commentId
                )
            )
        } else {
            viewModel.deleteComment(login, repo, commentId, type, number)
        }
    }

    override fun onEditComment(comment: String?, commentId: Int?, type: TimelineType) {
        viewModel.editComment(login, repo, comment, commentId?.toLong(), type, number)
    }

    override fun onAppBarScrollChanged(show: Boolean) {
        if (!reviewFab.isVisible) return
        if (show) {
            reviewFab.shrink()
        } else {
            reviewFab.extend()
        }
    }

    companion object {
        const val TAG = "IssueFragment"
        fun newInstance(
            login: String,
            repo: String,
            number: Int
        ) = PullRequestFragment().apply {
            arguments = bundleOf(EXTRA to login, EXTRA_TWO to repo, EXTRA_THREE to number)
        }
    }
}
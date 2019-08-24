package com.fastaccess.github.ui.modules.issuesprs

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.model.parcelable.EditIssuePrBundleModel
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.LoginRepoParcelableModel
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.extensions.*
import com.fastaccess.github.platform.mentions.MentionsPresenter
import com.fastaccess.github.ui.modules.comment.CommentActivity
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrActivity
import com.fastaccess.github.ui.modules.issuesprs.edit.assignees.AssigneesFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.LabelsFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.lockunlock.LockUnlockFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.milestone.MilestoneFragment
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.utils.EDITOR_DEEPLINK
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_THREE
import com.fastaccess.github.utils.EXTRA_TWO
import com.fastaccess.github.utils.extensions.isConnected
import com.fastaccess.github.utils.extensions.popMenu
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import com.google.android.material.appbar.AppBarLayout
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.CharPolicy
import github.type.IssueState
import github.type.LockReason
import kotlinx.android.synthetic.main.comment_box_layout.*
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.issue_header_row_item.*
import kotlinx.android.synthetic.main.issue_pr_view_layout.*
import kotlinx.android.synthetic.main.recyclerview_fastscroll_empty_state_layout.*
import kotlinx.android.synthetic.main.title_toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 2019-08-17.
 */
abstract class BaseIssuePrTimelineFragment : BaseFragment(),
    LockUnlockFragment.OnLockReasonSelected,
    LabelsFragment.OnLabelSelected,
    AssigneesFragment.OnAssigneesSelected,
    MilestoneFragment.OnMilestoneChanged {

    @Inject lateinit var mentionsPresenter: MentionsPresenter

    protected val login by lazy { arguments?.getString(EXTRA) ?: "" }
    protected val repo by lazy { arguments?.getString(EXTRA_TWO) ?: "" }
    protected val number by lazy { arguments?.getInt(EXTRA_THREE) ?: 0 }

    abstract val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    abstract fun reload(refresh: Boolean = false)
    abstract fun isPr(): Boolean
    abstract fun sendComment(comment: String)
    abstract fun lockIssuePr(lockReason: LockReason?)
    abstract fun onMilestoneAdd(timeline: TimelineModel)
    abstract fun editIssuerPr(
        title: String? = null,
        description: String? = null
    )

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        if (isPr()) {
            setupToolbar("", R.menu.issue_menu)// todo
        } else {
            setupToolbar("", R.menu.issue_menu)
        }
        swipeRefresh.appBarLayout = appBar
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, p1 ->
            val scrollTop = toolbar.menu?.findItem(R.id.scrollTop)
            val isVisible = p1 < 0
            if (isVisible && scrollTop?.isVisible == false) {
                scrollTop.isVisible = true
            } else if (!isVisible && scrollTop?.isVisible == true) {
                scrollTop.isVisible = false
            }
        })
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, appBar)
        recyclerView.adapter = adapter
        recyclerView.addOnLoadMore { isConnected().isTrue { reload() } }
        if (savedInstanceState == null) {
            isConnected().isTrue { reload(true) }
        }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                reload(true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        setupEditText()
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
                    commentText.setText(data?.getStringExtra(EXTRA))
                    sendComment.callOnClick()
                }
                EDIT_ISSUE_REQUEST_CODE -> {
                    val model = data?.getParcelableExtra<EditIssuePrBundleModel>(EXTRA) ?: return
                    editIssuerPr(model.title, model.description)
                }
                EDIT_COMMENT_REQUEST_CODE -> {
                    val comment = data?.getStringExtra(EXTRA)
                    val commentId = data?.getIntExtra(EXTRA_TWO, 0)
                    onEditComment(comment, commentId)
                }
                else -> Timber.e("nothing yet for requestCode($requestCode)")
            }
        }
    }

    override fun onLockReasonSelected(lockReason: LockReason?) {
        lockIssuePr(lockReason)
    }

    override fun onLabelsSelected(labelList: List<LabelModel>?) {
        labelsLayout.isVisible = !labelList.isNullOrEmpty()
        val builder = SpannableBuilder.builder()
        labelList?.forEach {
            builder.append(it.name ?: "", LabelSpan(Color.parseColor("#${it.color}")))
                .append(" ")
        }
        labels.text = builder
    }

    override fun onAssigneesSelected(assigneesList: List<ShortUserModel>?) {
        assigneesLayout.isVisible = !assigneesList.isNullOrEmpty()
        val builder = SpannableBuilder.builder()
        assigneesList?.forEachIndexed { index, item ->
            builder.clickable("@${item.login ?: item.name ?: ""}", View.OnClickListener {
                route(item.url)
            }).append(if (index == assigneesList.size.minus(1)) "" else ", ")
        }
        assignees.text = builder
    }

    override fun onMilestoneAdded(
        timeline: TimelineModel,
        model: MilestoneModel
    ) {
        onMilestoneAdd(timeline)
        model.let {
            milestoneLayout.isVisible = true
            milestone.text = when {
                it.title != null -> "${it.title}"
                it.description != null -> "${it.description}"
                else -> ""
            }
        }
    }

    protected fun setupEditText() {
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
                sendComment(comment)
            }
        }
        toggleFullScreen.setOnClickListener {
            routeForResult(EDITOR_DEEPLINK, COMMENT_REQUEST_CODE, bundleOf(EXTRA to commentText.text?.toString()))
        }
    }

    protected fun menuClick(
        model: IssueModel,
        isOwner: Boolean
    ) {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.scrollTop -> {
                    appBar.setExpanded(true, true)
                    recyclerView.scrollToPosition(0)
                }
                R.id.refresh -> {
                    reload(true)
                    appBar.setExpanded(true, true)
                    recyclerView.scrollToPosition(0)
                }
                R.id.closeIssue -> closeOpenIssuePr()
                R.id.share -> requireActivity().shareUrl(model.url)
                R.id.lockIssue -> if (item.title == getString(R.string.lock_issue)) {
                    MultiPurposeBottomSheetDialog.show(childFragmentManager, MultiPurposeBottomSheetDialog.BottomSheetFragmentType.LOCK_UNLOCK)
                } else {
                    lockUnlockIssuePr()
                }
                R.id.labels -> MultiPurposeBottomSheetDialog.show(
                    childFragmentManager,
                    MultiPurposeBottomSheetDialog.BottomSheetFragmentType.LABELS, LoginRepoParcelableModel(login, repo, model.labels, number)
                )
                R.id.assignees -> MultiPurposeBottomSheetDialog.show(
                    childFragmentManager,
                    MultiPurposeBottomSheetDialog.BottomSheetFragmentType.ASSIGNEES, LoginRepoParcelableModel(login, repo, model.assignees, number)
                )
                R.id.milestone -> MultiPurposeBottomSheetDialog.show(
                    childFragmentManager,
                    MultiPurposeBottomSheetDialog.BottomSheetFragmentType.MILESTONE, LoginRepoParcelableModel(login, repo, model.assignees, number)
                )
                R.id.edit -> startEditingIssue(model, isOwner)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun startEditingIssue(model: IssueModel, isOwner: Boolean) {
        EditIssuePrActivity.startForResult(
            this, EditIssuePrBundleModel(
                login, repo, number, model.title, model.body, false, isOwner = isOwner
            ), EDIT_ISSUE_REQUEST_CODE
        )
    }

    protected open fun lockUnlockIssuePr() = Unit
    protected open fun closeOpenIssuePr() = Unit

    protected fun initAssignees(assigneesList: List<ShortUserModel>?) {
        assigneesLayout.isVisible = !assigneesList.isNullOrEmpty()
        val builder = SpannableBuilder.builder()
        assigneesList?.forEachIndexed { index, item ->
            builder.clickable("@${item.login ?: item.name ?: ""}", View.OnClickListener {
                route(item.url)
            }).append(if (index == assigneesList.size.minus(1)) "" else ", ")
        }
        assignees.text = builder
    }

    protected fun initMilestone(model: MilestoneModel?) {
        model?.let {
            milestoneLayout.isVisible = true
            milestone.text = when {
                it.title != null -> "${it.title}"
                it.description != null -> "${it.description}"
                else -> ""
            }
        } ?: run { milestoneLayout.isVisible = false }
    }

    protected fun initLabels(labelList: List<LabelModel>?) {
        labelsLayout.isVisible = !labelList.isNullOrEmpty()
        val builder = SpannableBuilder.builder()
        labelList?.forEach {
            builder.append(it.name ?: "", LabelSpan(Color.parseColor("#${it.color}")))
                .append(" ")
        }
        labels.text = builder
    }

    protected fun initToolbarMenu(
        isOwner: Boolean,
        canUpdate: Boolean,
        viewerDidAuthor: Boolean? = null,
        isLocked: Boolean? = null,
        isMerged: Boolean? = null,
        state: String? = null
    ) {
        toolbar.menu.let {
            it.findItem(R.id.edit).isVisible = viewerDidAuthor == true || canUpdate
            it.findItem(R.id.assignees).isVisible = isOwner
            it.findItem(R.id.milestone).isVisible = isOwner
            it.findItem(R.id.labels).isVisible = isOwner
            it.findItem(R.id.closeIssue).isVisible = isOwner
            it.findItem(R.id.lockIssue).isVisible = isOwner
            it.findItem(R.id.closeIssue).title = if (!IssueState.OPEN.rawValue().equals(state, true)) {
                getString(R.string.re_open_issue)
            } else {
                getString(R.string.close_issue)
            }
            it.findItem(R.id.lockIssue).title = if (isLocked == true) getString(R.string.unlock_issue) else getString(R.string.lock_issue)
        }
    }

    protected open fun onCommentClicked(): (position: Int, comment: CommentModel) -> Unit = { position, comment ->
        CommentActivity.startActivity(
            this, COMMENT_REQUEST_CODE, comment.body ?: "",
            comment.author?.login ?: comment.author?.name, comment.author?.avatarUrl
        )
    }

    protected open fun onEditCommentClicked(): (position: Int, comment: CommentModel) -> Unit = { position, comment -> }
    protected open fun onDeleteCommentClicked(): (position: Int, comment: CommentModel) -> Unit = { position, comment -> }
    protected open fun onEditComment(comment: String?, commentId: Int?) = Unit

    companion object {
        const val COMMENT_REQUEST_CODE = 1001
        const val EDIT_ISSUE_REQUEST_CODE = 1002
        const val EDIT_COMMENT_REQUEST_CODE = 1003
    }
}
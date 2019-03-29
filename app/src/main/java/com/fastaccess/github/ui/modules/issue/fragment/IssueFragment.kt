package com.fastaccess.github.ui.modules.issue.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.model.getEmoji
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.LoginRepoParcelableModel
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.shareUrl
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.IssueTimelineAdapter
import com.fastaccess.github.ui.modules.issue.fragment.viewmodel.IssueTimelineViewModel
import com.fastaccess.github.ui.modules.issuesprs.edit.LockUnlockFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.assignees.AssigneesFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.LabelsFragment
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_THREE
import com.fastaccess.github.utils.EXTRA_TWO
import com.fastaccess.github.utils.extensions.*
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import github.type.LockReason
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.issue_header_row_item.*
import kotlinx.android.synthetic.main.issue_pr_fragment_layout.*
import net.nightwhistler.htmlspanner.HtmlSpanner
import javax.inject.Inject

/**
 * Created by Kosh on 28.01.19.
 */
class IssueFragment : BaseFragment(), LockUnlockFragment.OnLockReasonSelected,
    LabelsFragment.OnLabelSelected, AssigneesFragment.OnAssigneesSelected {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var htmlSpanner: HtmlSpanner
    @Inject lateinit var preference: FastHubSharedPreference

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(IssueTimelineViewModel::class.java) }
    private val login by lazy { arguments?.getString(EXTRA) ?: "" }
    private val repo by lazy { arguments?.getString(EXTRA_TWO) ?: "" }
    private val number by lazy { arguments?.getInt(EXTRA_THREE) ?: 0 }
    private val adapter by lazy { IssueTimelineAdapter(htmlSpanner, preference.theme) }

    override fun layoutRes(): Int = R.layout.issue_pr_fragment_layout
    override fun viewModel(): BaseViewModel? = viewModel

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        swipeRefresh.appBarLayout = appBar
        setupToolbar("${getString(R.string.issue)}#$number")
        bottomBar.inflateMenu(R.menu.issue_menu)
        bottomBar.menu.children.forEach {
            it.icon.mutate().setTint(Color.WHITE)
        }
        bottomBar.overflowIcon?.mutate()?.setTint(Color.WHITE)
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView, appBar)
        recyclerView.adapter = adapter
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.loadData(login, repo, number) } }
        if (savedInstanceState == null) {
            isConnected().isTrue { viewModel.loadData(login, repo, number, true) }
        }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.loadData(login, repo, number, true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        observeChanges()
    }

    override fun onLockReasonSelected(lockReason: LockReason?) {
        viewModel.lockUnlockIssue(login, repo, number, lockReason, true)
    }

    override fun onLabelsSelected(labels: List<LabelModel>?) {
        initLabels(labels)
        //TODO update adapter
    }

    override fun onAssigneesSelected(assignees: List<ShortUserModel>?) {
        viewModel.onAssigneesChanged(assignees, login, repo, number)
        //TODO update adapter
    }

    private fun menuClick(model: IssueModel) {
        bottomBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.scrollTop -> appBar.setExpanded(true, true)
                R.id.refresh -> {
                    viewModel.loadData(login, repo, number, true)
                    appBar.setExpanded(true, true)
                }
                R.id.closeIssue -> viewModel.closeOpenIssue(login, repo, number)
                R.id.share -> requireActivity().shareUrl(model.url)
                R.id.lockIssue -> if (item.title == getString(R.string.lock_issue)) {
                    MultiPurposeBottomSheetDialog.show(childFragmentManager, MultiPurposeBottomSheetDialog.BottomSheetFragmentType.LOCK_UNLOCK)
                } else {
                    viewModel.lockUnlockIssue(login, repo, number)
                }
                R.id.labels -> MultiPurposeBottomSheetDialog.show(childFragmentManager,
                    MultiPurposeBottomSheetDialog.BottomSheetFragmentType.LABELS, LoginRepoParcelableModel(login, repo, model.labels))
                R.id.assignees -> MultiPurposeBottomSheetDialog.show(childFragmentManager,
                    MultiPurposeBottomSheetDialog.BottomSheetFragmentType.ASSIGNEES, LoginRepoParcelableModel(login, repo, model.assignees, number))
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun observeChanges() {
        viewModel.getIssue(login, repo, number).observeNotNull(this) {
            initIssue(it)
        }
        viewModel.timeline.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    private fun initIssue(model: IssueModel) {
        val theme = preference.theme
        title.text = model.title
        opener.text = SpannableBuilder.builder()
            .bold(model.author?.login)
            .append(" opened this issue ")
            .append(model.createdAt?.timeAgo())

        userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url ?: "")
        author.text = model.author?.login
        association.text = if ("NONE" == model.authorAssociation) {
            model.updatedAt?.timeAgo()
        } else {
            "${model.authorAssociation?.toLowerCase()?.replace("_", "")} ${model.updatedAt?.timeAgo()}"
        }
        MarkdownProvider.loadIntoTextView(htmlSpanner, description, model.bodyHTML ?: "", ThemeEngine.getCodeBackground(theme),
            ThemeEngine.isLightTheme(theme))
        state.text = model.state?.toLowerCase()
        state.setChipBackgroundColorResource(if ("OPEN".equals(model.state, true)) {
            R.color.material_green_700
        } else {
            R.color.material_red_700
        })
        addEmoji.setOnClickListener {
            it.popupEmoji(requireNotNull(model.id), model.reactionGroups) {
                initReactions(model)
            }
        }
        menuClick(model)
        initReactions(model)
        initLabels(model.labels)
        initAssignees(model.assignees)
        initMilestone(model)
        bottomBar.menu.let {
            it.findItem(R.id.closeIssue).title = if (!"OPEN".equals(model.state, true)) {
                getString(R.string.re_open_issue)
            } else {
                getString(R.string.close_issue)
            }
            it.findItem(R.id.lockIssue).title = if (model.locked == true) getString(R.string.unlock_issue) else getString(R.string.lock_issue)
        }
    }

    private fun initAssignees(assigneesList: List<ShortUserModel>?) {
        assigneesLayout.isVisible = !assigneesList.isNullOrEmpty()
        val builder = SpannableBuilder.builder()
        assigneesList?.forEachIndexed { index, item ->
            builder.clickable(item.login ?: item.name ?: "", View.OnClickListener {
                it.context.route(item.url)
            }).append(if (index == assigneesList.size.minus(1)) "" else ", ")
        }
        assignees.text = builder
    }

    private fun initMilestone(model: IssueModel) {
        model.milestone?.let {
            milestoneLayout.isVisible = true
            milestone.text = when {
                it.title != null -> "${it.title}"
                it.description != null -> "${it.description}"
                else -> ""
            }
        } ?: kotlin.run { milestoneLayout.isVisible = false }
    }

    private fun initLabels(labelList: List<LabelModel>?) {
        labelsLayout.isVisible = !labelList.isNullOrEmpty()
        val builder = SpannableBuilder.builder()
        labelList?.forEach {
            builder.append(it.name ?: "", LabelSpan(Color.parseColor("#${it.color}")))
                .append(" ")
        }
        labels.text = builder
    }

    private fun initReactions(model: IssueModel) {
        reactionsText.isVisible = model.reactionGroups?.any { it.users?.totalCount != 0 } ?: false
        if (reactionsText.isVisible) {
            val stringBuilder = StringBuilder()
            model.reactionGroups?.forEach {
                if (it.users?.totalCount != 0) {
                    stringBuilder.append(it.content.getEmoji())
                        .append(" ")
                        .append("${it.users?.totalCount}")
                        .append("   ")
                }
            }
            reactionsText.text = stringBuilder
        } else {
            reactionsText.text = ""
        }
    }

    companion object {
        const val TAG = "IssueFragment"
        fun newInstance(login: String, repo: String, number: Int) = IssueFragment().apply {
            arguments = bundleOf(EXTRA to login, EXTRA_TWO to repo, EXTRA_THREE to number)
        }
    }
}
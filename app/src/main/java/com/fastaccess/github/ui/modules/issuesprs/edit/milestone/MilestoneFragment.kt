package com.fastaccess.github.ui.modules.issuesprs.edit.milestone

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.LoginRepoParcelableModel
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.MilestonesAdapter
import com.fastaccess.github.ui.modules.issuesprs.edit.milestone.viewmodel.MilestoneViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.isConnected
import kotlinx.android.synthetic.main.appbar_center_title_round_background_layout.*
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import java.util.*
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class MilestoneFragment : BaseFragment(), CreateMilestoneDialogFragment.OnAddNewMilestone {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(MilestoneViewModel::class.java) }
    private val model by lazy { arguments?.getParcelable(EXTRA) as? LoginRepoParcelableModel<LabelModel> }
    private val adapter by lazy {
        MilestonesAdapter {
            viewModel.onSubmit(it)
        }
    }

    private var callback: OnMilestoneChanged? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is OnMilestoneChanged -> context
            parentFragment is OnMilestoneChanged -> parentFragment as OnMilestoneChanged
            parentFragment?.parentFragment is OnMilestoneChanged -> parentFragment?.parentFragment as OnMilestoneChanged // deep hierarchy
            else -> null
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.rounded_toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        val login = model?.login
        val repo = model?.repo
        val number = model?.number

        if (login == null || repo == null || number == null) {
            dismiss()
            return
        }
        setupToolbar(R.string.milestones, R.menu.edit_submit_menu) { item: MenuItem ->
            when (item.itemId) {
                R.id.add -> {
                    CreateMilestoneDialogFragment().show(childFragmentManager, "CreateMilestoneDialogFragment")
                }
            }
        }
        toolbar.menu?.findItem(R.id.submit)?.isVisible = false
        recyclerView.addDivider()
        recyclerView.adapter = adapter
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        if (savedInstanceState == null) isConnected().isTrue { viewModel.load(login, repo, true) }
        swipeRefresh.setOnRefreshListener {
            if (isConnected()) {
                recyclerView.resetScrollState()
                viewModel.load(login, repo, true)
            } else {
                swipeRefresh.isRefreshing = false
            }
        }
        recyclerView.addOnLoadMore { isConnected().isTrue { viewModel.load(login, repo) } }
        listenToChanges()
    }

    override fun addNewMilestone(title: String, dueOn: Date, description: String?) {
        viewModel.addMilestone(title, dueOn, description, model?.login, model?.repo)
    }

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun newInstance(model: LoginRepoParcelableModel<LabelModel>?) = MilestoneFragment().apply {
            arguments = bundleOf(EXTRA to model)
        }
    }

    interface OnMilestoneChanged {
        fun onMilestoneAdded(milestone: MilestoneModel)
    }
}
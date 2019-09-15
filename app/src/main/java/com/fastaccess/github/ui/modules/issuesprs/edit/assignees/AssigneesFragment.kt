package com.fastaccess.github.ui.modules.issuesprs.edit.assignees

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.evernote.android.state.State
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.model.parcelable.LoginRepoParcelableModel
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.adapter.AssigneesAdapter
import com.fastaccess.github.ui.modules.issuesprs.edit.assignees.viewmodel.AssigneesViewModel

import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class AssigneesFragment : com.fastaccess.github.base.BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @State var selection = hashSetOf<ShortUserModel>()
    @State var deselection = hashSetOf<ShortUserModel>()
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(AssigneesViewModel::class.java) }
    private val model by lazy { arguments?.getParcelable(EXTRA) as? LoginRepoParcelableModel<ShortUserModel> }
    private val adapter by lazy {
        AssigneesAdapter(selection, deselection).apply {
            model?.items?.forEach { this.selection.add(it) }
        }
    }

    private var callback: OnAssigneesSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is OnAssigneesSelected -> context
            parentFragment is OnAssigneesSelected -> parentFragment as OnAssigneesSelected
            parentFragment?.parentFragment is OnAssigneesSelected -> parentFragment?.parentFragment as OnAssigneesSelected // deep hierarchy
            else -> null
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.rounded_toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        val login = model?.login
        val repo = model?.repo
        val number = model?.number

        if (login == null || repo == null || number == null) {
            dismiss()
            return
        }
        setupToolbar(R.string.assignees, R.menu.edit_submit_menu) { item: MenuItem ->
            when (item.itemId) {
                R.id.submit -> {
                    if (adapter.selection.toList() != model?.items) {
                        viewModel.addAssignees(login, repo, number,
                            adapter.selection.toList().map { it.login ?: "" },
                            adapter.deselection.toList().map { it.login ?: "" })
                    }
                }
            }
        }
        toolbar?.menu?.findItem(R.id.add)?.isVisible = false
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

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
        viewModel.additionLiveData.observeNotNull(this) {
            if (it == true) {
                callback?.onAssigneesSelected(adapter.selection.toList())
                dismiss()
            }
        }
    }


    companion object {
        fun newInstance(model: LoginRepoParcelableModel<ShortUserModel>?) = AssigneesFragment().apply {
            arguments = bundleOf(EXTRA to model)
        }
    }

    interface OnAssigneesSelected {
        fun onAssigneesSelected(assignees: List<ShortUserModel>?)
    }
}
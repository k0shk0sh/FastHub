package com.fastaccess.github.ui.modules.issuesprs.edit.labels

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.evernote.android.state.State
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.LoginRepoParcelableModel
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.isConnected
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.show
import com.fastaccess.github.ui.adapter.LabelsAdapter
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.create.CreateLabelFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.viewmodel.LabelsViewModel

import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class LabelsFragment : com.fastaccess.github.base.BaseFragment(), CreateLabelFragment.OnCreateLabelCallback {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @State var selection = hashSetOf<LabelModel>()
    @State var deselection = hashSetOf<LabelModel>()
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(LabelsViewModel::class.java) }
    private val model by lazy { arguments?.getParcelable(EXTRA) as? LoginRepoParcelableModel<LabelModel> }
    private val adapter by lazy {
        LabelsAdapter(selection, deselection).apply {
            model?.items?.forEach { this.selection.add(it) }
        }
    }

    private var callback: OnLabelSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is OnLabelSelected -> context
            parentFragment is OnLabelSelected -> parentFragment as OnLabelSelected
            parentFragment?.parentFragment is OnLabelSelected -> parentFragment?.parentFragment as OnLabelSelected // deep hierarchy
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
        setupToolbar(R.string.labels, R.menu.edit_submit_menu) { item: MenuItem ->
            when (item.itemId) {
                R.id.submit -> {
                    if (adapter.selection.toList() != model?.items) {
                        viewModel.putLabels(login, repo, number, adapter.selection, adapter.deselection)
                    } else {
                        dismiss()
                    }
                }
                R.id.add -> {
                    CreateLabelFragment.newInstance().show(childFragmentManager)
                }
            }
        }
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

    override fun onCreateLabel(name: String, color: String) {
        val login = model?.login
        val repo = model?.repo

        if (login == null || repo == null) {
            dismiss()
            return
        }
        viewModel.addLabel(login, repo, name, color)
    }

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
        }
        viewModel.putLabelsLiveData.observeNotNull(this) {
            callback?.onLabelsSelected(adapter.selection.toList())
            dismiss()
        }
    }


    companion object {
        fun newInstance(model: LoginRepoParcelableModel<LabelModel>?) = LabelsFragment().apply {
            arguments = bundleOf(EXTRA to model)
        }
    }

    interface OnLabelSelected {
        fun onLabelsSelected(labels: List<LabelModel>?)
    }
}
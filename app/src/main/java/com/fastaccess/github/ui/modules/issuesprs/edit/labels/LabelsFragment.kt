package com.fastaccess.github.ui.modules.issuesprs.edit.labels

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.LoginRepoParcelableModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.LabelsAdapter
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.viewmodel.LabelsViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.isConnected
import kotlinx.android.synthetic.main.empty_state_layout.*
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-26.
 */
class LabelsFragment : BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(LabelsViewModel::class.java) }
    private val model by lazy { arguments?.getParcelable(EXTRA) as? LoginRepoParcelableModel<LabelModel> }
    private val adapter by lazy {
        LabelsAdapter().apply {
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

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.rounded_toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        val login = model?.login
        val repo = model?.repo

        if (login == null || repo == null) {
            dismiss()
            return
        }
        setupToolbar(R.string.labels, R.menu.edit_submit_menu) { item: MenuItem ->
            when (item.itemId) {
                R.id.submit -> {
                    callback?.onLabelsSelected(adapter.selection.toList())
                    dismiss()
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

    private fun listenToChanges() {
        viewModel.data.observeNotNull(this) {
            adapter.submitList(it)
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
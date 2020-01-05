package com.fastaccess.fasthub.commit.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.adapter.SimpleListAdapter
import com.fastaccess.github.base.extensions.addDivider
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.show

class CommitListBottomSheetDialog : BaseBottomSheetDialogFragment() {

    private var commitListCallback: CommitListCallback? = null
    private val map by lazy { arguments?.getSerializable(EXTRA) as? HashMap<String, String> }
    private val adapter by lazy {
        SimpleListAdapter<String> {
            commitListCallback?.onCommitClicked(map?.get(it) ?: "")
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        commitListCallback = when {
            parentFragment is CommitListCallback -> parentFragment as CommitListCallback
            context is CommitListCallback -> context
            else -> throw IllegalAccessError("your parent fragment or activity must implement CommitListCallback")
        }
    }

    override fun onDetach() {
        commitListCallback = null
        super.onDetach()
    }

    override fun viewModel(): BaseViewModel? = null

    override fun layoutRes(): Int = R.layout.rounded_toolbar_fragment_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.commits)
        swipeRefresh.isEnabled = false
        recyclerView.adapter = adapter
        recyclerView.addDivider()
        recyclerView.setEmptyView(emptyLayout)
        fastScroller.attachRecyclerView(recyclerView)
        map?.let { adapter.submitList(it.keys.toList()) }
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            map: HashMap<String, String>
        ) = CommitListBottomSheetDialog().apply {
            arguments = bundleOf().apply { putSerializable(EXTRA, map) }
            show(fragmentManager)
        }
    }
}

interface CommitListCallback {
    fun onCommitClicked(url: String)
}
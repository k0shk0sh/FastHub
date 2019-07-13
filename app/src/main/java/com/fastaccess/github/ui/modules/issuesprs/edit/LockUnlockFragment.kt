package com.fastaccess.github.ui.modules.issuesprs.edit

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import github.type.LockReason
import kotlinx.android.synthetic.main.appbar_center_title_round_background_layout.*
import kotlinx.android.synthetic.main.lock_unlock_issue_pr_layout.*

/**
 * Created by Kosh on 23.02.19.
 */
class LockUnlockFragment : BaseFragment() {

    private var callback: OnLockReasonSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is OnLockReasonSelected -> context
            parentFragment is OnLockReasonSelected -> parentFragment as OnLockReasonSelected
            parentFragment?.parentFragment is OnLockReasonSelected -> parentFragment?.parentFragment as OnLockReasonSelected // deep hierarchy
            else -> null
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun layoutRes(): Int = R.layout.lock_unlock_issue_pr_layout
    override fun viewModel(): BaseViewModel? = null

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbarTitle.text = getString(R.string.lock_issue)
        submit.setOnClickListener {
            callback?.onLockReasonSelected(when (reason.checkedChipId) {
                R.id.spam -> LockReason.SPAM
                R.id.tooHeated -> LockReason.TOO_HEATED
                R.id.offTopic -> LockReason.OFF_TOPIC
                R.id.resolved -> LockReason.RESOLVED
                else -> null
            })
            dismiss()
        }
    }

    interface OnLockReasonSelected {
        fun onLockReasonSelected(lockReason: LockReason?)
    }

    companion object {
        fun newInstance() = LockUnlockFragment()
    }
}
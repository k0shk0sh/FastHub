package com.fastaccess.github.ui.modules.multipurpose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.transaction
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.UserOrgsFragment

/**
 * Created by Kosh on 2018-11-25.
 */
class MultiPurposeBottomSheetDialog : BaseBottomSheetDialogFragment() {

    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            childFragmentManager.transaction {
                replace(R.id.container, UserOrgsFragment.newInstance(), "UserOrgsFragment")
            }
        }
    }

    override fun viewModel(): BaseViewModel? = null

    companion object {
        fun show(fragmentManager: FragmentManager) {
            MultiPurposeBottomSheetDialog().show(fragmentManager, "MultiPurposeBottomSheetDialog")
        }
    }
}
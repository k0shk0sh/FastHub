package com.fastaccess.github.ui.modules.multipurpose

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.transaction
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.UserOrgsFragment
import com.fastaccess.github.utils.EXTRA

/**
 * Created by Kosh on 2018-11-25.
 */
class MultiPurposeBottomSheetDialog : BaseBottomSheetDialogFragment() {

    override fun layoutRes(): Int = R.layout.fragment_activity_layout
    private val type by lazy { arguments?.getSerializable(EXTRA) as? BottomSheetFragmentType? }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            childFragmentManager.transaction {
                when (type) {
                    BottomSheetFragmentType.ORGANIZATIONS -> replace(R.id.container, UserOrgsFragment.newInstance(), "UserOrgsFragment")
                    null -> dismiss()
                }
            }
        }
    }

    override fun viewModel(): BaseViewModel? = null

    companion object {
        fun show(fragmentManager: FragmentManager, type: BottomSheetFragmentType) {
            MultiPurposeBottomSheetDialog()
                .apply {
                    arguments = bundleOf(EXTRA to type)
                    show(fragmentManager, "MultiPurposeBottomSheetDialog")
                }
        }
    }

    enum class BottomSheetFragmentType {
        ORGANIZATIONS
    }
}
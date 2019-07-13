package com.fastaccess.github.ui.modules.multipurpose

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.data.model.parcelable.FilterTrendingModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.show
import com.fastaccess.github.ui.modules.issuesprs.edit.LockUnlockFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.assignees.AssigneesFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.labels.LabelsFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.milestone.MilestoneFragment
import com.fastaccess.github.ui.modules.issuesprs.filter.FilterIssuesPrsBottomSheet
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog.BottomSheetFragmentType.*
import com.fastaccess.github.ui.modules.profile.orgs.userorgs.UserOrgsFragment
import com.fastaccess.github.ui.modules.search.filter.FilterSearchBottomSheet
import com.fastaccess.github.ui.modules.trending.filter.FilterTrendingBottomSheet
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_TWO

/**
 * Created by Kosh on 2018-11-25.
 */
class MultiPurposeBottomSheetDialog : BaseBottomSheetDialogFragment() {

    override fun layoutRes(): Int = R.layout.fragment_activity_layout
    private val type by lazy { arguments?.getSerializable(EXTRA) as? BottomSheetFragmentType? }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                when (type) {
                    ORGANIZATIONS -> replace(R.id.container, UserOrgsFragment.newInstance(), "UserOrgsFragment")
                    FILTER_ISSUES, FILTER_PRS ->
                        replace(R.id.container, FilterIssuesPrsBottomSheet
                            .newInstance(arguments?.getParcelable(EXTRA_TWO) ?: FilterIssuesPrsModel()), "FilterIssuesPrsBottomSheet")
                    FILTER_SEARCH -> replace(R.id.container, FilterSearchBottomSheet
                        .newInstance(arguments?.getParcelable(EXTRA_TWO) ?: FilterSearchModel()), "FilterSearchBottomSheet")
                    TRENDING -> replace(R.id.container,
                        FilterTrendingBottomSheet.newInstance(arguments?.getParcelable(EXTRA_TWO)
                            ?: FilterTrendingModel()), "FilterTrendingBottomSheet")
                    LOCK_UNLOCK -> replace(R.id.container, LockUnlockFragment.newInstance(), "LockUnlockFragment")
                    LABELS -> replace(R.id.container,
                        LabelsFragment.newInstance(arguments?.getParcelable(EXTRA_TWO)), "LabelsFragment")
                    ASSIGNEES -> replace(R.id.container,
                        AssigneesFragment.newInstance(arguments?.getParcelable(EXTRA_TWO)), "AssigneesFragment")
                    MILESTONE -> replace(R.id.container,
                        MilestoneFragment.newInstance(arguments?.getParcelable(EXTRA_TWO)), "MilestoneFragment")
                    null -> dialog?.dismiss()
                }
            }
        }
    }

    override fun viewModel(): BaseViewModel? = null

    companion object {
        fun show(fragmentManager: FragmentManager, type: BottomSheetFragmentType, parcelable: Parcelable? = null) {
            MultiPurposeBottomSheetDialog()
                .apply {
                    arguments = bundleOf(EXTRA to type).apply {
                        if (parcelable != null) putParcelable(EXTRA_TWO, parcelable)
                    }
                    show(fragmentManager)
                }
        }
    }

    enum class BottomSheetFragmentType {
        ORGANIZATIONS, FILTER_ISSUES, FILTER_PRS, FILTER_SEARCH,
        TRENDING, LOCK_UNLOCK, LABELS, ASSIGNEES, MILESTONE
    }
}
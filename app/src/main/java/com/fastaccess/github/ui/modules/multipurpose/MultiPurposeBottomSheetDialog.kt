package com.fastaccess.github.ui.modules.multipurpose

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.data.model.parcelable.FilterSearchModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.issuesprs.filter.FilterIssuesPrsBottomSheet
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
                    BottomSheetFragmentType.ORGANIZATIONS -> replace(R.id.container, UserOrgsFragment.newInstance(), "UserOrgsFragment")
                    BottomSheetFragmentType.FILTER_ISSUES, BottomSheetFragmentType.FILTER_PRS ->
                        replace(R.id.container, FilterIssuesPrsBottomSheet
                            .newInstance(arguments?.getParcelable(EXTRA_TWO) ?: FilterIssuesPrsModel()), "FilterIssuesPrsBottomSheet")
                    BottomSheetFragmentType.FILTER_SEARCH -> replace(R.id.container, FilterSearchBottomSheet
                        .newInstance(arguments?.getParcelable(EXTRA_TWO) ?: FilterSearchModel()), "FilterSearchBottomSheet")
                    BottomSheetFragmentType.TRENDING -> replace(R.id.container, FilterTrendingBottomSheet.newInstance(), "FilterTrendingBottomSheet")
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
                    show(fragmentManager, "MultiPurposeBottomSheetDialog")
                }
        }
    }

    enum class BottomSheetFragmentType {
        ORGANIZATIONS, FILTER_ISSUES, FILTER_PRS, FILTER_SEARCH, TRENDING
    }
}
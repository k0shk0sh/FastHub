package com.fastaccess.github.ui.modules.search.fragment

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.modules.multipurpose.MultiPurposeBottomSheetDialog
import kotlinx.android.synthetic.main.search_fragment_layout.*

/**
 * Created by Kosh on 20.01.19.
 */
class SearchFragment : BaseFragment() {
    override fun layoutRes(): Int = R.layout.search_fragment_layout
    override fun viewModel(): BaseViewModel? = null

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        filter.setOnClickListener {
            MultiPurposeBottomSheetDialog.show(childFragmentManager, MultiPurposeBottomSheetDialog.BottomSheetFragmentType.FILTER_SEARCH,
                null/*TODO*/)
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
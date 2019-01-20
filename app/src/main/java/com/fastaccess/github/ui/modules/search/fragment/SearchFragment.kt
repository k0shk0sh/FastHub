package com.fastaccess.github.ui.modules.search.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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

        clear.setOnClickListener { searchEditText.setText("") }

        searchEditText.doAfterTextChanged { text ->
            val show = !text.isNullOrEmpty()
            if (show != clear.isVisible) { // prevent multiple hide/show
                clear.isVisible = show
            }
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
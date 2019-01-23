package com.fastaccess.github.ui.modules.trending.filter

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fastaccess.data.model.LanguageColorsModel
import com.fastaccess.extension.uiThread
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.ui.adapter.LanguagesAdapter
import com.fastaccess.github.utils.extensions.addDivider
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import kotlinx.android.synthetic.main.filter_trending_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 23.01.19.
 */
class FilterTrendingBottomSheet : BaseFragment() {

    @Inject lateinit var gson: Gson

    private var callback: FilterTrendingCallback? = null
    private val adapter by lazy { LanguagesAdapter() }


    override fun layoutRes(): Int = R.layout.filter_trending_layout
    override fun viewModel(): BaseViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            context is FilterTrendingCallback -> context
            parentFragment is FilterTrendingCallback -> parentFragment as FilterTrendingCallback
            parentFragment?.parentFragment is FilterTrendingCallback -> parentFragment?.parentFragment as FilterTrendingCallback // deep hierarchy
            else -> null
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.filter)

        languageRecyclerView.adapter = adapter
        languageRecyclerView.addDivider()

        addDisposal(LanguageColorsModel.newInstance(gson, requireContext())
            .uiThread()
            .subscribe({ adapter.submitList(it) }, { it.printStackTrace() }))

        submit.setOnClickListener {
            callback?.onFilterApplied(adapter.checkedLanguage, if (sinceGroup.checkedChipId != -1) {
                sinceGroup.findViewById<Chip>(sinceGroup.checkedChipId)?.text?.toString() ?: ""
            } else {
                "daily"
            })
            (parentFragment as? BaseBottomSheetDialogFragment)?.dismiss()
        }
    }


    companion object {
        fun newInstance(lan: String? = null, since: String? = null): FilterTrendingBottomSheet {
            return FilterTrendingBottomSheet()
        }
    }

    interface FilterTrendingCallback {
        fun onFilterApplied(lan: String, since: String)
    }
}
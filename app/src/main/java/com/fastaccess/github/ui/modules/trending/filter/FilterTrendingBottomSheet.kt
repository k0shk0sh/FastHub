package com.fastaccess.github.ui.modules.trending.filter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.fastaccess.data.model.LanguageColorsModel
import com.fastaccess.data.model.parcelable.FilterTrendingModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.addDivider
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.ui.adapter.LanguagesAdapter
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import kotlinx.android.synthetic.main.filter_trending_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 23.01.19.
 */
class FilterTrendingBottomSheet : com.fastaccess.github.base.BaseFragment() {

    @Inject lateinit var gson: Gson
    @Inject lateinit var schedulerProvider: SchedulerProvider

    private var callback: FilterTrendingCallback? = null
    private val adapter by lazy { LanguagesAdapter() }
    private val model by lazy { arguments?.getParcelable(EXTRA) ?: FilterTrendingModel() }


    override fun layoutRes(): Int = R.layout.filter_trending_layout
    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = null

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

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
        setupToolbar(R.string.filter)
        adapter.checkedLanguage = model.lang
        languageRecyclerView.adapter = adapter
        languageRecyclerView.addDivider()
        sinceGroup.check(
            when (model.since) {
                FilterTrendingModel.TrendingSince.DAILY -> R.id.daily
                FilterTrendingModel.TrendingSince.WEEKLY -> R.id.weekly
                FilterTrendingModel.TrendingSince.MONTHLY -> R.id.monthly
            }
        )
        addDisposal(LanguageColorsModel.newInstance(gson, requireContext())
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .subscribe({ adapter.submitList(it) }, { showSnackBar(view, message = it.message) })
        )

        submit.setOnClickListener {
            model.lang = adapter.checkedLanguage
            model.since = if (sinceGroup.checkedChipId != -1) {
                FilterTrendingModel.TrendingSince.getSince(sinceGroup.findViewById<Chip>(sinceGroup.checkedChipId)?.text?.toString())
            } else {
                FilterTrendingModel.TrendingSince.DAILY
            }
            callback?.onFilterApplied(model)
            dismiss()
        }
    }


    companion object {
        fun newInstance(model: FilterTrendingModel): FilterTrendingBottomSheet {
            return FilterTrendingBottomSheet().apply {
                arguments = bundleOf(EXTRA to model)
            }
        }
    }

    interface FilterTrendingCallback {
        fun onFilterApplied(model: FilterTrendingModel)
    }
}
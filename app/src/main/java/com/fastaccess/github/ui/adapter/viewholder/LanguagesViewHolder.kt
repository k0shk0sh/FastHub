package com.fastaccess.github.ui.adapter.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.model.LanguageColorsModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.LanguagesAdapter
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.extensions.getColorAttr
import kotlinx.android.synthetic.main.language_row_item.view.*

/**
 * Created by Kosh on 2018-11-17.
 */
class LanguagesViewHolder(
    private val languagesAdapter: LanguagesAdapter,
    parent: ViewGroup
) : BaseViewHolder<LanguageColorsModel>(LayoutInflater.from(parent.context).inflate(R.layout.language_row_item, parent, false)) {

    override fun bind(item: LanguageColorsModel) {
        itemView.apply {
            languageChip.text = item.name
            languageChip.isChecked = languagesAdapter.checkedLanguage == item.name
            languageChip.checkedIcon?.mutate()?.setTint(if (item.color.isNullOrEmpty()) {
                context.getColorAttr(R.attr.colorAccent)
            } else {
                kotlin.runCatching { Color.parseColor(item.color) }.getOrDefault(context.getColorAttr(R.attr.colorAccent))
            })
        }
    }

}
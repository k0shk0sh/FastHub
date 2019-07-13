package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.LanguageColorsModel
import com.fastaccess.github.ui.adapter.viewholder.LanguagesViewHolder
import kotlinx.android.synthetic.main.language_row_item.view.*

/**
 * Created by Kosh on 20.01.19.
 */
class LanguagesAdapter : ListAdapter<LanguageColorsModel, LanguagesViewHolder>(DIFF_CALLBACK) {

    var checkedLanguage = "All"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagesViewHolder = LanguagesViewHolder(this, parent).apply {
        itemView.languageChip.let { chip ->
            chip.setOnClickListener {
                val isChecked = chip.isChecked
                checkedLanguage = if (isChecked) {
                    chip.text?.toString() ?: "All"
                } else {
                    "All"
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: LanguagesViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LanguageColorsModel?>() {
            override fun areItemsTheSame(oldItem: LanguageColorsModel, newItem: LanguageColorsModel): Boolean = oldItem.name == newItem.name
            override fun areContentsTheSame(oldItem: LanguageColorsModel, newItem: LanguageColorsModel): Boolean = oldItem == newItem
        }
    }
}

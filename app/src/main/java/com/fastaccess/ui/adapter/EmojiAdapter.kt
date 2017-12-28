package com.fastaccess.ui.adapter

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.fastaccess.ui.adapter.viewholder.EmojiViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import ru.noties.markwon.extension.emoji.loader.EmojiModel

/**
 * Created by kosh on 17/08/2017.
 */
class EmojiAdapter(listener: BaseViewHolder.OnItemClickListener<EmojiModel>)
    : BaseRecyclerAdapter<EmojiModel, EmojiViewHolder, BaseViewHolder.OnItemClickListener<EmojiModel>>(listener), Filterable {

    var copiedList = mutableListOf<EmojiModel>()

    override fun viewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        return EmojiViewHolder.newInstance(parent, this)
    }

    override fun onBindView(holder: EmojiViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                if (copiedList.isEmpty()) {
                    copiedList.addAll(data)
                }
                val text = constraint.toString().toLowerCase()
                val filteredResults: List<EmojiModel> = if (text.isNotBlank()) {
                    val data = data.filter {
                        text in it.tags || it.description.contains(text) ||
                                it.unicode.contains(text) || text in it.aliases
                    }
                    if (data.isNotEmpty()) data
                    else copiedList
                } else {
                    copiedList
                }
                val results = FilterResults()
                results.values = filteredResults
                results.count = filteredResults.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(var1: CharSequence, results: Filter.FilterResults) {
                results.values?.let {
                    insertItems(it as List<EmojiModel>)
                }
            }
        }
    }
}
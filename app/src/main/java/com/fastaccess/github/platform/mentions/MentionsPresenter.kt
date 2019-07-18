package com.fastaccess.github.platform.mentions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.usecase.search.FilterSearchUsersUseCase
import com.otaliastudios.autocomplete.RecyclerViewPresenter
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-18.
 */
class MentionsPresenter @Inject constructor(
    c: Context,
    private val searchUsersUseCase: FilterSearchUsersUseCase
) : RecyclerViewPresenter<String>(c) {

    private val localList = ArrayList<String>()

    private val adapter by lazy { MentionsAdapter(this, arrayListOf()) }

    override fun instantiateAdapter(): RecyclerView.Adapter<*> = adapter

    override fun onQuery(query: CharSequence?) {
        Timber.e("$query")
        if (!query.isNullOrEmpty()) {
            if (localList.firstOrNull { it.contains(query) } != null) {
                Timber.e("current list has the query($query)")
//                searchUsersUseCase.dispose()
                return
            }
            searchUsersUseCase.keyword = query.toString()
            searchUsersUseCase.executeSafely(searchUsersUseCase.buildObservable()
                .map { result -> result.second.map { it.login ?: it.name ?: "" } }
                .doOnNext {
                    Timber.e("result($it)")
                    localList.clear()
                    setUsers(it)
                })
        }
    }

    fun setUsers(newList: List<String>) {
        localList.addAll(newList)
        adapter.newList(localList.distinct())
    }

    fun onClick(item: String) = dispatchClick(item)

    fun onDispose() = searchUsersUseCase.dispose()
}

class MentionsAdapter(
    private val presenter: MentionsPresenter,
    private val list: ArrayList<String>
) : RecyclerView.Adapter<MentionsAdapter.MentionsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MentionsViewHolder = MentionsViewHolder(
        LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_list_item_1,
            parent, false
        )
    ).apply {
        itemView.setOnClickListener { presenter.onClick(list.getOrNull(adapterPosition) ?: "") }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: MentionsViewHolder,
        position: Int
    ) = holder.bind(list.getOrNull(position) ?: "")

    fun newList(newList: List<String>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class MentionsViewHolder(view: View) : BaseViewHolder<String>(view) {
        override fun bind(item: String) {
            itemView.findViewById<TextView>(android.R.id.text1).text = item
        }
    }
}

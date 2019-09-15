package com.fastaccess.github.editor.presenter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.editor.usecase.FilterSearchUsersUseCase
import com.fastaccess.github.extensions.isTrue
import com.otaliastudios.autocomplete.RecyclerViewPresenter
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-18.
 */
class MentionsPresenter @Inject constructor(
    c: Context,
    private val searchUsersUseCase: FilterSearchUsersUseCase,
    schedulerProvider: SchedulerProvider
) : RecyclerViewPresenter<String>(c) {

    private var disposable: Disposable? = null
    private val localList = ArrayList<String>()
    private val publishSubject by lazy { PublishSubject.create<String>() }

    init {
        disposable = publishSubject.debounce(400, TimeUnit.MILLISECONDS)
            .switchMap { query ->
                searchUsersUseCase.keyword = query
                return@switchMap searchUsersUseCase.buildObservable()
                    .map { result -> result.second.map { it.login ?: it.name ?: "" } }
            }
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .subscribe({
                Timber.e("result($it)")
                localList.clear()
                setUsers(it)
            }, {
                it.printStackTrace()
            })
    }

    private val adapter by lazy { MentionsAdapter(this, arrayListOf()) }
    var isMatchParent = false

    override fun instantiateAdapter(): RecyclerView.Adapter<*> = adapter

    override fun onQuery(query: CharSequence?) {
        Timber.e("$query")
        if (!query.isNullOrEmpty()) {
            if (localList.firstOrNull { it.contains(query) } != null) {
                return
            }
            publishSubject.onNext(query.toString())
        }
    }

    fun setUsers(newList: List<String>) {
        localList.addAll(newList)
        adapter.newList(localList.filter { it.isNotBlank() }.distinct())
    }

    fun onClick(item: String) = dispatchClick(item)

    fun onDispose() {
        if (disposable?.isDisposed == false) {
            disposable?.dispose()
        }
    }

    override fun getPopupDimensions(): PopupDimensions {
        return super.getPopupDimensions().apply {
            isMatchParent.isTrue { height = LinearLayout.LayoutParams.MATCH_PARENT }
        }
    }
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

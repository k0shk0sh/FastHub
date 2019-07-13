package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.persistence.models.UserPinnedRepoNodesModel
import com.fastaccess.github.ui.adapter.viewholder.ProfilePinnedReposViewHolder

/**
 * Created by Kosh on 2018-11-17.
 */
class ProfilePinnedReposAdapter(private val list: ArrayList<UserPinnedRepoNodesModel>) : RecyclerView.Adapter<ProfilePinnedReposViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePinnedReposViewHolder = ProfilePinnedReposViewHolder(parent)
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ProfilePinnedReposViewHolder, position: Int) = holder.bind(list[position])

    fun insertNew(list: List<UserPinnedRepoNodesModel>) {
        this.list.apply {
            clear()
            addAll(list)
            notifyDataSetChanged()
        }
    }
}
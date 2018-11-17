package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.persistence.models.UserOrganizationNodesModel
import com.fastaccess.github.ui.adapter.viewholder.ProfileOrgsViewHolder

/**
 * Created by Kosh on 2018-11-17.
 */
class ProfileOrgsAdapter(private val list: ArrayList<UserOrganizationNodesModel>) : RecyclerView.Adapter<ProfileOrgsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileOrgsViewHolder = ProfileOrgsViewHolder(parent)
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ProfileOrgsViewHolder, position: Int) = holder.bind(list[position])

    fun insertNew(list: List<UserOrganizationNodesModel>) {
        this.list.apply {
            clear()
            addAll(list)
            notifyDataSetChanged()
        }
    }
}
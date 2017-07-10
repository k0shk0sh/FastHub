package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.bindView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 09 Jul 2017, 4:54 PM
 */

class LoginViewHolder private constructor(itemView: View, adapter: BaseRecyclerAdapter<*, *, *>?) :
        BaseViewHolder<Login>(itemView, adapter) {
    val avatarLayout: AvatarLayout by bindView(R.id.avatarLayout)
    val title: FontTextView by bindView(R.id.title)

    override fun bind(login: Login) {
        avatarLayout.setUrl(login.avatarUrl, login.login, false, false)
        title.text = login.login
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): LoginViewHolder {
            return LoginViewHolder(BaseViewHolder.getView(parent, R.layout.login_row_item), adapter)
        }
    }
}

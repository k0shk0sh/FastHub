package com.fastaccess.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.ui.adapter.CommitCommentsAdapter
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView

/**
 * Created by kosh on 15/08/2017.
 */
class CommitThreadViewHolder private constructor(view: View,
                                                 adapter: BaseRecyclerAdapter<*, *, *>,
                                                 val onToggleView: OnToggleView)
    : BaseViewHolder<TimelineModel>(view, adapter), BaseViewHolder.OnItemClickListener<Comment> {

    @BindView(R.id.pathText) lateinit var pathText: FontTextView
    @BindView(R.id.toggle) lateinit var toggle: View
    @BindView(R.id.toggleHolder) lateinit var toggleHolder: View
    @BindView(R.id.commitComments) lateinit var commitComments: DynamicRecyclerView

    init {
        toggleHolder.setOnClickListener(this)
        toggle.setOnClickListener(this)
        itemView.setOnClickListener(null)
        itemView.setOnLongClickListener(null)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.toggle || v.id == R.id.toggleHolder) {
            val position = adapterPosition
            onToggleView.onToggle(position.toLong(), !onToggleView.isCollapsed(position.toLong()))
            onToggle(onToggleView.isCollapsed(position.toLong()))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bind(model: TimelineModel) {
        val t = model.commit
        t?.let {
            val builder = SpannableBuilder.builder()
            pathText.text = builder.append("${if (!it.login.isNullOrBlank()) it.login else ""} commented on")
                    .append(if (!it.path.isNullOrEmpty()) {
                        " ${it.path}#L${it.position} in "
                    } else {
                        " "
                    })
                    .url(it.commitId.substring(0, 7))
            it.comments?.let {
                if (!it.isEmpty()) commitComments.adapter = CommitCommentsAdapter(it, this, onToggleView)
            }
        }
        onToggle(onToggleView.isCollapsed(adapterPosition.toLong()))
    }


    private fun onToggle(expanded: Boolean) {
        toggle.rotation = if (!expanded) 0.0f else 180f
        commitComments.visibility = if (!expanded) View.GONE
        else if (commitComments.adapter != null) View.VISIBLE
        else View.GONE
    }

    override fun onItemClick(position: Int, v: View?, item: Comment) {}

    override fun onItemLongClick(position: Int, v: View?, item: Comment) {}

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>,
                        onToggleView: OnToggleView): CommitThreadViewHolder {
            return CommitThreadViewHolder(getView(parent, R.layout.grouped_commit_comment_row), adapter, onToggleView)
        }
    }
}
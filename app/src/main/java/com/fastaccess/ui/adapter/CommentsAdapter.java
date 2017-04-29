package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.viewholder.CommentsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class CommentsAdapter extends BaseRecyclerAdapter<Comment, CommentsViewHolder, BaseViewHolder.OnItemClickListener<Comment>> {

    private final OnToggleView onToggleView;
    private final String login;
    private final boolean showEmojies;

    public CommentsAdapter(@NonNull ArrayList<Comment> eventsModels, @NonNull OnToggleView onToggleView, boolean showEmojies) {
        super(eventsModels);
        this.onToggleView = onToggleView;
        this.showEmojies = showEmojies;
        this.login = Login.getUser().getLogin();
    }

    @Override protected CommentsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return CommentsViewHolder.newInstance(parent, this, login, onToggleView, showEmojies);
    }

    @Override protected void onBindView(CommentsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}

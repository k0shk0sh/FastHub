package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.ui.adapter.viewholder.ReviewCommentsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class ReviewCommentsAdapter extends BaseRecyclerAdapter<ReviewCommentModel, ReviewCommentsViewHolder, BaseViewHolder
        .OnItemClickListener<ReviewCommentModel>> {


    public ReviewCommentsAdapter(@NonNull List<ReviewCommentModel> eventsModels) {
        super(eventsModels);
    }

    @Override protected ReviewCommentsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return ReviewCommentsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(ReviewCommentsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}

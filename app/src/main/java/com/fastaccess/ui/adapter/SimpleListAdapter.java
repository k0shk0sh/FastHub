package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.ui.adapter.viewholder.SimpleViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

public class SimpleListAdapter<O> extends BaseRecyclerAdapter<O, SimpleViewHolder<O>,
        SimpleViewHolder.OnItemClickListener<O>> {
    public SimpleListAdapter(@NonNull List<O> data, @Nullable SimpleViewHolder.OnItemClickListener<O> listener) {
        super(data, listener);
    }

    @Override protected SimpleViewHolder<O> viewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder<>(BaseViewHolder.getView(parent, R.layout.simple_row_item), this);
    }

    @Override protected void onBindView(SimpleViewHolder<O> holder, int position) {
        holder.bind(getItem(position));
    }
}
package com.fastaccess.ui.widgets.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Kosh on 17 May 2016, 7:13 PM
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public interface OnItemClickListener<T> {
        void onItemClick(int position, View v, T item);

        void onItemLongClick(int position, View v, T item);
    }

    @Nullable protected final BaseRecyclerAdapter adapter;

    public static View getView(@NonNull ViewGroup parent, @LayoutRes int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    protected BaseViewHolder(@NonNull View itemView) {
        this(itemView, null);
    }

    public BaseViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @SuppressWarnings("unchecked") @Override public void onClick(View v) {
        if (adapter != null && adapter.getListener() != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && position < adapter.getItemCount())
                adapter.getListener().onItemClick(position, v, adapter.getItem(position));
        }
    }

    @SuppressWarnings("unchecked") @Override public boolean onLongClick(View v) {
        if (adapter != null && adapter.getListener() != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && position < adapter.getItemCount())
                adapter.getListener().onItemLongClick(position, v, adapter.getItem(position));
        }
        return true;
    }

    public abstract void bind(@NonNull T t);

    protected void onViewIsDetaching() {}

}

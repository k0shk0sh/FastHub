package com.fastaccess.ui.widgets.recyclerview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.fastaccess.helper.AnimHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 17 May 2016, 7:10 PM
 */
public abstract class BaseRecyclerAdapter<M, VH extends BaseViewHolder,
        P extends BaseViewHolder.OnItemClickListener<M>> extends RecyclerView.Adapter<VH> {

    @NonNull private List<M> data;
    @Nullable private P listener;
    private int lastKnowingPosition = -1;
    private boolean enableAnimation = true;

    public BaseRecyclerAdapter() {
        this(new ArrayList<>());
    }

    public BaseRecyclerAdapter(@NonNull List<M> data) {
        this(data, null);
    }

    public BaseRecyclerAdapter(@NonNull List<M> data, @Nullable P listener) {
        this.data = data;
        this.listener = listener;
    }

    protected abstract VH viewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindView(VH holder, int position);

    @NonNull public List<M> getData() {
        return data;
    }

    public M getItem(int position) {
        return data.get(position);
    }

    public int getItem(M t) {
        return data.indexOf(t);
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        animate(holder);
        onBindView(holder, position);
    }

    @Override public int getItemCount() {
        return data.size();
    }

    private void animate(VH holder) {
        int position = holder.getLayoutPosition();
        if (isEnableAnimation() && position > lastKnowingPosition) {
            AnimHelper.startBeatsAnimation(holder.itemView);
            lastKnowingPosition = position;
        }
    }

    public void insertItems(List<M> items) {
        data.clear();
        addItems(items);
    }

    public void addItem(M item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeInserted(position, getItemCount());
    }

    public void addItem(M item) {
        data.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    @SuppressWarnings("WeakerAccess") public void addItems(List<M> items) {
        data.addAll(items);
        notifyDataSetChanged();
    }

    @SuppressWarnings("WeakerAccess") public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(M item) {
        int position = data.indexOf(item);
        removeItem(position);
    }

    public void removeItems(List<M> items) {
//        int prevSize = data.size();
        data.removeAll(items);
        notifyDataSetChanged();
//        notifyItemRangeRemoved(prevSize, Math.abs(data.size() - prevSize));
    }

    public void swapItem(M model) {
        int index = getItem(model);
        swapItem(model, index);
    }

    public void swapItem(M model, int position) {
        data.set(position, model);
        notifyDataSetChanged();
    }

    public void subList(int fromPosition, int toPosition) {
        data.subList(fromPosition, toPosition).clear();
        notifyItemRangeRemoved(fromPosition, toPosition);
    }

    public void clear() {
        data.clear();
        notifyItemRangeRemoved(0, getItemCount());
    }

    public void setEnableAnimation(boolean enableAnimation) {
        this.enableAnimation = enableAnimation;
        notifyDataSetChanged();
    }

    @SuppressWarnings("WeakerAccess") public boolean isEnableAnimation() {
        return enableAnimation;
    }

    @SuppressWarnings("WeakerAccess") @Nullable public P getListener() {
        return listener;
    }

    public void setListener(@Nullable P listener) {
        this.listener = listener;
        notifyDataSetChanged();
    }
}

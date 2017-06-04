package com.fastaccess.ui.widgets.recyclerview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.PrefGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 17 May 2016, 7:10 PM
 */
public abstract class BaseRecyclerAdapter<M, VH extends BaseViewHolder,
        P extends BaseViewHolder.OnItemClickListener<M>> extends RecyclerView.Adapter<VH> {

    public interface GuideListener<M> {
        void onShowGuide(@NonNull View itemView, @NonNull M model);
    }

    @NonNull private List<M> data;
    @Nullable private P listener;
    private int lastKnowingPosition = -1;
    private boolean enableAnimation = PrefGetter.isRVAnimationEnabled();
    private boolean showedGuide;
    private GuideListener guideListener;

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

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        animate(holder, position);
        onBindView(holder, position);
        onShowGuide(holder, position);
    }

    @Override public int getItemCount() {
        return data.size();
    }

    @SuppressWarnings("unchecked")
    protected void onShowGuide(@NonNull VH holder, int position) {// give the flexibility to other adapters to override this
        if (position == 0 && !isShowedGuide() && guideListener != null) {
            guideListener.onShowGuide(holder.itemView, getItem(position));
            showedGuide = true;
        }
    }

    private void animate(@NonNull VH holder, int position) {
        if (isEnableAnimation() && position > lastKnowingPosition) {
            AnimHelper.startBeatsAnimation(holder.itemView);
            lastKnowingPosition = position;
        }
    }

    public void insertItems(@NonNull List<M> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(M item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public void addItem(M item) {
        data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    @SuppressWarnings("WeakerAccess") public void addItems(@NonNull List<M> items) {
        data.addAll(items);
        notifyItemRangeInserted(getItemCount(), (getItemCount() + items.size()) - 1);
    }

    @SuppressWarnings("WeakerAccess") public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(M item) {
        int position = data.indexOf(item);
        if (position != -1) removeItem(position);
    }

    public void removeItems(@NonNull List<M> items) {
        int prevSize = getItemCount();
        data.removeAll(items);
        notifyItemRangeRemoved(prevSize, Math.abs(data.size() - prevSize));
    }

    public void swapItem(@NonNull M model) {
        int index = getItem(model);
        swapItem(model, index);
    }

    public void swapItem(@NonNull M model, int position) {
        if (position != -1) {
            data.set(position, model);
            notifyItemChanged(position);
        }
    }

    public void subList(int fromPosition, int toPosition) {
        if (data.isEmpty()) return;
        data.subList(fromPosition, toPosition).clear();
        notifyItemRangeRemoved(fromPosition, toPosition);
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return data.isEmpty();
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

    public void setGuideListener(GuideListener guideListener) {
        this.guideListener = guideListener;
    }

    public boolean isShowedGuide() {
        return showedGuide;
    }
}

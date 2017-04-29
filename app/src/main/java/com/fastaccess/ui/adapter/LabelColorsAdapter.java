package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.ui.adapter.viewholder.LabelColorsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 02 Apr 2017, 5:19 PM
 */

public class LabelColorsAdapter extends BaseRecyclerAdapter<String, LabelColorsViewHolder, BaseViewHolder.OnItemClickListener<String>> {

    public LabelColorsAdapter(@NonNull List<String> data, @Nullable BaseViewHolder.OnItemClickListener<String> listener) {
        super(data, listener);
    }

    @Override protected LabelColorsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return LabelColorsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(LabelColorsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}

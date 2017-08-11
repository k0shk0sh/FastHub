package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.ui.adapter.viewholder.LabelsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class LabelsAdapter extends BaseRecyclerAdapter<LabelModel, LabelsViewHolder, BaseViewHolder
        .OnItemClickListener<LabelModel>> {

    public interface OnSelectLabel {
        boolean isLabelSelected(LabelModel labelModel);

        void onToggleSelection(LabelModel labelModel, boolean select);
    }

    @Nullable private OnSelectLabel onSelectLabel;

    public LabelsAdapter(@NonNull List<LabelModel> eventsModels, @Nullable OnSelectLabel onSelectLabel) {
        super(eventsModels);
        this.onSelectLabel = onSelectLabel;
    }

    @Override protected LabelsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return LabelsViewHolder.newInstance(parent, onSelectLabel, this);
    }

    @Override protected void onBindView(LabelsViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}

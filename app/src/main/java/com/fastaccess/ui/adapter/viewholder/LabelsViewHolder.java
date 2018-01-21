package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 22 Feb 2017, 7:36 PM
 */

public class LabelsViewHolder extends BaseViewHolder<LabelModel> {


    @BindView(R.id.colorImage) AppCompatImageView colorImage;
    @BindView(R.id.name) FontTextView name;
    private LabelsAdapter.OnSelectLabel onSelectLabel;

    @Override public void onClick(View v) {
        if (onSelectLabel != null) {
            int position = getAdapterPosition();
            if (adapter != null) {
                LabelModel labelModel = (LabelModel) adapter.getItem(position);
                onSelectLabel.onToggleSelection(labelModel, !onSelectLabel.isLabelSelected(labelModel));
            }
        } else {
            super.onClick(v);
        }
    }

    private LabelsViewHolder(@NonNull View itemView, LabelsAdapter.OnSelectLabel onSelectLabel, @NonNull BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        this.onSelectLabel = onSelectLabel;
    }

    public static LabelsViewHolder newInstance(@NonNull ViewGroup parent, @Nullable LabelsAdapter.OnSelectLabel onSelectLabel,
                                               @NonNull BaseRecyclerAdapter adapter) {
        return new LabelsViewHolder(getView(parent, R.layout.label_row_item), onSelectLabel, adapter);
    }

    @Override public void bind(@NonNull LabelModel labelModel) {
        name.setText(labelModel.getName());
        if (labelModel.getColor() != null) {
            int color = Color.parseColor(labelModel.getColor().startsWith("#") ? labelModel.getColor() : "#" + labelModel.getColor());
            colorImage.setBackgroundColor(color);
            if (onSelectLabel != null) {
                if (onSelectLabel.isLabelSelected(labelModel)) {
                    name.setTextColor(ViewHelper.generateTextColor(color));
                } else {
                    name.setTextColor(ViewHelper.getPrimaryTextColor(itemView.getContext()));
                }
                itemView.setBackgroundColor(onSelectLabel.isLabelSelected(labelModel) ? color : 0);
            }
        } else {
            colorImage.setBackgroundColor(0);
        }
    }
}

package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.LabelsAdapter;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindColor;
import butterknife.BindView;

/**
 * Created by Kosh on 22 Feb 2017, 7:36 PM
 */

public class LabelsViewHolder extends BaseViewHolder<LabelModel> {


    @BindView(R.id.colorImage) AppCompatImageView colorImage;
    @BindView(R.id.name) FontTextView name;
    @BindColor(R.color.primary_text) int primaryTextColor;
    private LabelsAdapter.OnSelectLabel onSelectLabel;

    @Override public void onClick(View v) {
        int position = getAdapterPosition();
        onSelectLabel.onToggleSelection(position, !onSelectLabel.isLabelSelected(position));
    }

    private LabelsViewHolder(@NonNull View itemView, LabelsAdapter.OnSelectLabel onSelectLabel) {
        super(itemView);
        this.onSelectLabel = onSelectLabel;
    }

    public static LabelsViewHolder newInstance(@NonNull ViewGroup parent, @NonNull LabelsAdapter.OnSelectLabel onSelectLabel) {
        return new LabelsViewHolder(getView(parent, R.layout.label_row_item), onSelectLabel);
    }

    @Override public void bind(@NonNull LabelModel labelModel) {
        int color = Color.parseColor(labelModel.getColor().startsWith("#") ? labelModel.getColor() : "#" + labelModel.getColor());
        colorImage.setBackgroundColor(color);
        name.setText(labelModel.getName());
        if (onSelectLabel.isLabelSelected(getAdapterPosition())) {
            name.setTextColor(ViewHelper.generateTextColor(color));
        } else {
            name.setTextColor(primaryTextColor);
        }
        itemView.setBackgroundColor(onSelectLabel.isLabelSelected(getAdapterPosition()) ? color : 0);
    }
}

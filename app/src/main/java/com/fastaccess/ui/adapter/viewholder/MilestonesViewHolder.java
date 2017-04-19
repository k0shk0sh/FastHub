package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class MilestonesViewHolder extends BaseViewHolder<MilestoneModel> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.notificationTitle) FontTextView notificationTitle;

    private MilestonesViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static MilestonesViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter) {
        return new MilestonesViewHolder(getView(viewGroup, R.layout.milestone_row_item), adapter);
    }

    @Override public void bind(@NonNull MilestoneModel milestoneModel) {
        title.setText(milestoneModel.getTitle());
        notificationTitle.setText(milestoneModel.getDescription());
        if (milestoneModel.getDueOn() != null) {
            date.setText(ParseDateFormat.getTimeAgo(milestoneModel.getDueOn()));
        } else if (milestoneModel.getCreatedAt() != null) {
            date.setText(ParseDateFormat.getTimeAgo(milestoneModel.getCreatedAt()));
        }
    }
}
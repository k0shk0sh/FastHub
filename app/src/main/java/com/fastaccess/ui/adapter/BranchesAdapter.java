package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 09 Apr 2017, 10:55 AM
 */

public class BranchesAdapter extends BaseRecyclerAdapter<BranchesModel, BranchesAdapter.BranchesViewHolder, BaseViewHolder
        .OnItemClickListener<BranchesModel>> {

    public BranchesAdapter(@NonNull List<BranchesModel> data, @Nullable BaseViewHolder.OnItemClickListener<BranchesModel> listener) {
        super(data, listener);
    }

    @Override protected BranchesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return new BranchesViewHolder(BaseViewHolder.getView(parent, R.layout.branches_row_item), this);
    }

    @Override protected void onBindView(BranchesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class BranchesViewHolder extends BaseViewHolder<BranchesModel> {
        @Nullable @BindView(R.id.image) AppCompatImageView image;
        @BindView(android.R.id.text1) TextView title;

        BranchesViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
            super(itemView, adapter);
        }

        @Override public void bind(@NonNull BranchesModel branchesModel) {
            if (image != null) {
                image.setImageResource(branchesModel.isTag() ? R.drawable.ic_label : R.drawable.ic_branch);
                image.setContentDescription(branchesModel.getName());
            }
            title.setText(branchesModel.getName());
        }
    }
}

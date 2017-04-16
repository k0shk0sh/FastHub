package com.fastaccess.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.BranchesModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 09 Apr 2017, 10:55 AM
 */

public class BranchesAdapter extends BaseAdapter {

    private List<BranchesModel> branches;
    private Context context;

    public BranchesAdapter(Context context, List<BranchesModel> branches) {
        this.branches = branches;
        this.context = context;
    }

    @Override public int getCount() {
        return branches.size();
    }

    @Override public BranchesModel getItem(int position) {
        return branches.get(position);
    }

    @Override public long getItemId(int position) {
        return 0;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        return getRowView(position, convertView, parent, false);
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getRowView(position, convertView, parent, true);
    }

    @NonNull private View getRowView(int position, View convertView, ViewGroup parent, boolean isDropDown) {
        ViewHolder viewHolder;
        if (convertView == null) {
            if (!isDropDown) {
                convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.branches_row_item, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BranchesModel branchesModel = getItem(position);
        if (viewHolder.image != null && isDropDown) {
            viewHolder.image.setImageResource(branchesModel.isTag() ? R.drawable.ic_label : R.drawable.ic_branch);
            viewHolder.image.setContentDescription(branchesModel.getName());
        }
        viewHolder.title.setText(branchesModel.getName());
        return convertView;
    }

    static class ViewHolder {
        @Nullable @BindView(R.id.image) AppCompatImageView image;
        @BindView(android.R.id.text1) TextView title;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}

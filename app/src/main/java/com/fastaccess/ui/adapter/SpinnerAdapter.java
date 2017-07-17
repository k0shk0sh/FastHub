package com.fastaccess.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 09 Apr 2017, 10:55 AM
 */

public class SpinnerAdapter<O> extends BaseAdapter {

    private List<O> data;
    private Context context;

    public SpinnerAdapter(@NonNull Context context, @NonNull List<O> branches) {
        this.data = branches;
        this.context = context;
    }

    @Override public int getCount() {
        return data.size();
    }

    @Override public O getItem(int position) {
        return data.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
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
                convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(getItem(position).toString());
        return convertView;
    }

    static class ViewHolder {
        @BindView(android.R.id.text1) TextView title;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

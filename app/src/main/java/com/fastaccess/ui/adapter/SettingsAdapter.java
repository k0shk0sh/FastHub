package com.fastaccess.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastaccess.R;
import com.fastaccess.data.dao.SettingsModel;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JediB on 5/12/2017.
 */

public class SettingsAdapter extends BaseAdapter {

    private ArrayList<SettingsModel> settings;
    private final LayoutInflater inflater;

    public SettingsAdapter(@NonNull Context context, @NonNull ArrayList<SettingsModel> settings) {
        this.settings = settings;
        this.inflater = LayoutInflater.from(context);
    }

    @Override public int getCount() {
        return settings.size();
    }

    @Override public SettingsModel getItem(int position) {
        return settings.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.icon_row_item, parent, false);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        SettingsModel model = getItem(position);
        viewHolder.title.setText(model.getTitle());
        viewHolder.image.setImageResource(model.getImage());
        viewHolder.summary.setVisibility(View.GONE);
        return row;
    }

    static class ViewHolder {
        @BindView(R.id.iconItemImage) ForegroundImageView image;
        @BindView(R.id.iconItemTitle) FontTextView title;
        @BindView(R.id.iconItemSummary) FontTextView summary;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
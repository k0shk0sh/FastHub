package com.fastaccess.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Setting;
import com.fastaccess.helper.ActivityHelper;

/**
 * Created by JediB on 5/12/2017.
 */

public class SettingsAdapter extends BaseAdapter {

	private Setting[] settings;
	private Context context;

	public SettingsAdapter(Context context, Setting[] settings) {
		this.context = context;
		this.settings = settings;
	}

	@Override
	public int getCount() {

		return settings.length;
	}

	@Override
	public Setting getItem(int position) {
		return settings[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ActivityHelper.getActivity(context).getLayoutInflater();
		View row;
		row = inflater.inflate(R.layout.icon_row_item, parent, false);
		TextView title;
		TextView summary;
		ImageView image;
		image = (ImageView) row.findViewById(R.id.iconItemImage);
		summary = (TextView) row.findViewById(R.id.iconItemSummary);
		title = (TextView) row.findViewById(R.id.iconItemTitle);
		title.setText(settings[position].getTitle());
		summary.setText(settings[position].getSummary());
		image.setImageResource(settings[position].getImage());

		if(summary.getText().toString().length()<=0)
			summary.setVisibility(View.GONE);

		return row;
	}
}
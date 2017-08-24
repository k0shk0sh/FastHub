package com.fastaccess.ui.adapter;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.viewholder.SimpleViewHolder;
import com.fastaccess.ui.modules.search.SearchActivity;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 May 2017, 6:58 PM
 */

public class TopicsAdapter extends BaseRecyclerAdapter<String, SimpleViewHolder<String>, BaseViewHolder.OnItemClickListener<String>> {
    private boolean isLightTheme = true;
    @ColorInt private int cardBackground;

    public TopicsAdapter(@NonNull List<String> data) {
        super(data);
    }

    @Override protected SimpleViewHolder<String> viewHolder(ViewGroup parent, int viewType) {
        isLightTheme = !AppHelper.isNightMode(parent.getResources());
        cardBackground = ViewHelper.getCardBackground(parent.getContext());
        return new SimpleViewHolder<>(BaseViewHolder.getView(parent, R.layout.topics_row_item), null);
    }

    @Override protected void onBindView(SimpleViewHolder<String> holder, int position) {
        if (isLightTheme) {
            holder.itemView.setBackgroundColor(cardBackground);
        }
        String item = getItem(position);
        holder.itemView.setOnClickListener((view) -> {
            Intent intent = new Intent(new Intent(App.getInstance().getApplicationContext(), SearchActivity.class));
            intent.putExtra("search", "topic:\"" + item + "\"");
            view.getContext().startActivity(intent);
        });
        holder.bind(getItem(position));
    }
}

package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Dec 2016, 3:12 PM
 */

public class SimpleViewHolder<O> extends BaseViewHolder<O> {

    @BindView(R.id.title) FontTextView title;

    public SimpleViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    @Override public void bind(@NonNull O o) {
        title.setText(o.toString());
    }
}

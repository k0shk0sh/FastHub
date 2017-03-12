package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.SearchCodeModel;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class SearchCodeViewHolder extends BaseViewHolder<SearchCodeModel> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.details) FontTextView details;

    private SearchCodeViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static SearchCodeViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new SearchCodeViewHolder(getView(viewGroup, R.layout.issue_no_image_row_item), adapter);
    }

    @Override public void bind(@NonNull SearchCodeModel codeMode) {
        title.setText(codeMode.getRepository() != null ? codeMode.getRepository().getFullName() : "N/A");
        details.setText(codeMode.getName());
    }
}

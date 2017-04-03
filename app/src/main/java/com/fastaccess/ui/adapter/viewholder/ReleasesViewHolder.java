package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class ReleasesViewHolder extends BaseViewHolder<Release> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.details) FontTextView details;
    @BindView(R.id.download) ForegroundImageView download;
    @BindString(R.string.released) String released;
    @BindString(R.string.drafted) String drafted;

    private ReleasesViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        download.setOnClickListener(this);
        download.setOnLongClickListener(this);
    }

    public static ReleasesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new ReleasesViewHolder(getView(viewGroup, R.layout.releases_row_item), adapter);
    }

    @Override public void bind(@NonNull Release item) {
        title.setText(SpannableBuilder.builder().bold(!InputHelper.isEmpty(item.getName()) ? item.getName() : item.getTagName()));
        if (item.getAuthor() != null) {
            details.setText(SpannableBuilder.builder()
                    .append(item.getAuthor().getLogin())
                    .append(" ")
                    .append(item.isDraft() ? drafted : released)
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(item.getCreatedAt())));
        } else {
            details.setVisibility(View.GONE);
        }
    }
}

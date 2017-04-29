package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.RepoFile;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 15 Feb 2017, 10:29 PM
 */

public class RepoFilesViewHolder extends BaseViewHolder<RepoFile> {

    @BindView(R.id.contentTypeImage) ForegroundImageView contentTypeImage;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.size) FontTextView size;
    @BindView(R.id.menu) ForegroundImageView menu;
    @BindString(R.string.file) String file;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.contentTypeImage) {
            itemView.callOnClick();
        } else {
            super.onClick(v);
        }
    }

    private RepoFilesViewHolder(@NonNull View itemView, @NonNull BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        menu.setOnClickListener(this);
        contentTypeImage.setOnClickListener(this);
    }

    public static RepoFilesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new RepoFilesViewHolder(getView(viewGroup, R.layout.repo_files_row_item), adapter);
    }

    @Override public void bind(@NonNull RepoFile filesModel) {
        contentTypeImage.setContentDescription(String.format("%s %s", filesModel.getName(), file));
        title.setText(filesModel.getName());
        if (filesModel.getType() != null && filesModel.getType().getIcon() != 0) {
            contentTypeImage.setImageResource(filesModel.getType().getIcon());
            if (filesModel.getType() == FilesType.file) {
                size.setText(Formatter.formatFileSize(size.getContext(), filesModel.getSize()));
                size.setVisibility(View.VISIBLE);
            } else {
                size.setVisibility(View.GONE);
            }
        }
    }
}

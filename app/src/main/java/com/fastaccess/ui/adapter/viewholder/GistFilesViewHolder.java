package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.ui.adapter.GistFilesAdapter;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 12 Nov 2016, 3:44 PM
 */

public class GistFilesViewHolder extends BaseViewHolder<FilesListModel> {


    @BindView(R.id.fileName) FontTextView fileName;
    @BindView(R.id.language) FontTextView language;
    @BindView(R.id.size) FontTextView size;

    private GistFilesViewHolder(@NonNull View itemView, GistFilesAdapter adapter) {
        super(itemView, adapter);

    }

    public static GistFilesViewHolder newInstance(@NonNull ViewGroup parent, GistFilesAdapter adapter) {
        return new GistFilesViewHolder(getView(parent, R.layout.gist_files_row_item), adapter);
    }

    @Override public void bind(@NonNull FilesListModel filesListModel) {
        fileName.setText(filesListModel.getFilename());
        language.setText(SpannableBuilder.builder().bold(filesListModel.getType()));
        size.setText(Formatter.formatFileSize(size.getContext(), filesListModel.getSize()));
    }
}

package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 31 Dec 2016, 3:12 PM
 */

public class CommitLinesViewHolder extends BaseViewHolder<CommitLinesModel> {

    @BindView(R.id.textView) AppCompatTextView textView;
    @BindView(R.id.leftLinNo) AppCompatTextView leftLinNo;
    @BindView(R.id.rightLinNo) AppCompatTextView rightLinNo;
    @BindView(R.id.hasComment) View hasComment;
    private final int patchAdditionColor;
    private final int patchDeletionColor;
    private final int patchRefColor;

    private CommitLinesViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        patchAdditionColor = ViewHelper.getPatchAdditionColor(itemView.getContext());
        patchDeletionColor = ViewHelper.getPatchDeletionColor(itemView.getContext());
        patchRefColor = ViewHelper.getPatchRefColor(itemView.getContext());
        textView.setOnLongClickListener(this);
    }

    public static CommitLinesViewHolder newInstance(@NonNull ViewGroup viewGroup, @NonNull BaseRecyclerAdapter adapter) {
        return new CommitLinesViewHolder(getView(viewGroup, R.layout.commit_line_row_item), adapter);
    }

    @Override public void bind(@NonNull CommitLinesModel item) {
        leftLinNo.setText(item.getLeftLineNo() > 0 ? String.valueOf(item.getLeftLineNo()) : "  ");
        rightLinNo.setText(item.getRightLineNo() > 0 ? String.valueOf(item.getRightLineNo()) : "  ");
        hasComment.setVisibility(item.isHasCommentedOn() ? View.VISIBLE : View.GONE);
        switch (item.getColor()) {
            case CommitLinesModel.ADDITION:
                textView.setBackgroundColor(patchAdditionColor);
                break;
            case CommitLinesModel.DELETION:
                textView.setBackgroundColor(patchDeletionColor);
                break;
            case CommitLinesModel.PATCH:
                leftLinNo.setVisibility(View.GONE);
                rightLinNo.setVisibility(View.GONE);
                textView.setBackgroundColor(patchRefColor);
                break;
            default:
                textView.setBackgroundColor(Color.TRANSPARENT);
        }
        if (item.isNoNewLine()) {
            textView.setText(SpannableBuilder.builder().append(item.getText()).append(" ")
                    .append(ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_newline)));
        } else {
            textView.setText(item.getText());
        }
    }
}

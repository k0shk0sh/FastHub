package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.widgets.DiffLineSpan;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 15 Feb 2017, 10:29 PM
 */

public class CommitFilesViewHolder extends BaseViewHolder<CommitFileModel> {
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.patch) FontTextView patch;
    @BindView(R.id.changes) FontTextView changes;
    @BindView(R.id.addition) FontTextView addition;
    @BindView(R.id.deletion) FontTextView deletion;
    @BindView(R.id.status) FontTextView status;
    @BindView(R.id.toggle) View toggle;
    @BindView(R.id.open) View open;
    @BindString(R.string.changes) String changesText;
    @BindString(R.string.addition) String additionText;
    @BindString(R.string.delete) String deletionText;
    @BindString(R.string.status) String statusText;
    private final int patchAdditionColor;
    private final int patchDeletionColor;
    private final int patchRefColor;

    private String pathText;
    private OnToggleView onToggleView;

    @Override public void onClick(View v) {
        if (v.getId() != R.id.open) {
            int position = getAdapterPosition();
            onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
            onToggle(onToggleView.isCollapsed(position));
        } else {
            super.onClick(v);
        }
    }

    private void onToggle(boolean expanded) {
        if (!expanded) {
            patch.setText(".....");
            name.setMaxLines(2);
            toggle.setRotation(0.0f);
        } else {
            name.setMaxLines(5);
            setPatchText(pathText);
            toggle.setRotation(180f);
        }
    }

    private CommitFilesViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter,
                                  @NonNull OnToggleView onToggleView) {
        super(itemView, adapter);
        open.setOnClickListener(this);
        this.onToggleView = onToggleView;
        patchAdditionColor = ViewHelper.getPatchAdditionColor(itemView.getContext());
        patchDeletionColor = ViewHelper.getPatchDeletionColor(itemView.getContext());
        patchRefColor = ViewHelper.getPatchRefColor(itemView.getContext());
    }

    public static CommitFilesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter,
                                                    @NonNull OnToggleView onToggleView) {
        return new CommitFilesViewHolder(getView(viewGroup, R.layout.commit_file_row_item), adapter, onToggleView);
    }

    @Override public void bind(@NonNull CommitFileModel commit) {
        this.pathText = commit.getPatch();
        name.setText(commit.getFilename());
        changes.setText(SpannableBuilder.builder()
                .append(changesText)
                .append("\n")
                .bold(String.valueOf(commit.getChanges())));
        addition.setText(SpannableBuilder.builder()
                .append(additionText)
                .append("\n")
                .bold(String.valueOf(commit.getAdditions())));
        deletion.setText(SpannableBuilder.builder()
                .append(deletionText)
                .append("\n")
                .bold(String.valueOf(commit.getDeletions())));
        status.setText(SpannableBuilder.builder()
                .append(statusText)
                .append("\n")
                .bold(String.valueOf(commit.getStatus())));
        onToggle(onToggleView.isCollapsed(getAdapterPosition()));
    }

    private void setPatchText(@NonNull String text) {
        if (!TextUtils.isEmpty(text)) {
            String[] split = text.split("\\r?\\n|\\r");
            if (split.length > 0) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                int lines = split.length;
                for (int i = 0; i < lines; i++) {
                    String token = split[i];
                    if (i < (lines - 1)) {
                        token = token.concat("\n");
                    }
                    char firstChar = token.charAt(0);
                    int color = Color.TRANSPARENT;
                    if (firstChar == '+') {
                        color = patchAdditionColor;
                    } else if (firstChar == '-') {
                        color = patchDeletionColor;
                    } else if (token.startsWith("@@")) {
                        color = patchRefColor;
                    }
                    SpannableString spannableDiff = new SpannableString(token);
                    if (color != Color.TRANSPARENT) {
                        DiffLineSpan span = new DiffLineSpan(color, patch.getPaddingLeft());
                        spannableDiff.setSpan(span, 0, token.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    builder.append(spannableDiff);
                }
                patch.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                patch.setText(builder);
            }
        }
    }
}

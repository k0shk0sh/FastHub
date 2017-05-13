package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
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
    @BindString(R.string.deletion) String deletionText;
    @BindString(R.string.status) String statusText;
    private final int patchAdditionColor;
    private final int patchDeletionColor;
    private final int patchRefColor;
    private String pathText;
    private OnToggleView onToggleView;
    private ViewGroup viewGroup;

    @Override public void onClick(View v) {
        if (v.getId() != R.id.open) {
            int position = getAdapterPosition();
            onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
            onToggle(onToggleView.isCollapsed(position), true);
        } else {
            super.onClick(v);
        }
    }

    private CommitFilesViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                  @NonNull OnToggleView onToggleView) {
        super(itemView, adapter);
        this.viewGroup = viewGroup;
        open.setOnClickListener(this);
        this.onToggleView = onToggleView;
        patchAdditionColor = ViewHelper.getPatchAdditionColor(itemView.getContext());
        patchDeletionColor = ViewHelper.getPatchDeletionColor(itemView.getContext());
        patchRefColor = ViewHelper.getPatchRefColor(itemView.getContext());
    }

    public static CommitFilesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter,
                                                    @NonNull OnToggleView onToggleView) {
        return new CommitFilesViewHolder(getView(viewGroup, R.layout.commit_file_row_item), viewGroup, adapter, onToggleView);
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
        onToggle(onToggleView.isCollapsed(getAdapterPosition()), false);
    }

    private void setPatchText(@NonNull String text) {
        patch.setText(DiffLineSpan.getSpannable(text, patchAdditionColor, patchDeletionColor, patchRefColor));
    }

    private void onToggle(boolean expanded, boolean animate) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, new ChangeBounds());
        }
        if (!expanded) {
            patch.setText("");
            name.setMaxLines(2);
            toggle.setRotation(0.0f);
        } else {
            name.setMaxLines(5);
            setPatchText(pathText);
            toggle.setRotation(180f);
        }
    }
}

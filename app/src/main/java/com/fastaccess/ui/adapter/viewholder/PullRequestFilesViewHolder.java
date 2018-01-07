package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.adapter.CommitLinesAdapter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindString;
import butterknife.BindView;
import es.dmoral.toasty.Toasty;

/**
 * Created by Kosh on 15 Feb 2017, 10:29 PM
 */

public class PullRequestFilesViewHolder extends BaseViewHolder<CommitFileChanges> implements
        BaseViewHolder.OnItemClickListener<CommitLinesModel> {

    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.patchList) DynamicRecyclerView patch;
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
    private OnToggleView onToggleView;
    private ViewGroup viewGroup;
    @Nullable private PullRequestFilesMvp.OnPatchClickListener onPatchClickListener;

    @Override public void onClick(View v) {
        if (v.getId() != R.id.open) {
            int position = getAdapterPosition();
            onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
            onToggle(onToggleView.isCollapsed(position), true, position);
        } else {
            super.onClick(v);
        }
    }

    private PullRequestFilesViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                       @NonNull OnToggleView onToggleView, @Nullable PullRequestFilesMvp.OnPatchClickListener onPatchClickListener) {
        super(itemView, adapter);
        this.viewGroup = viewGroup;
        this.onToggleView = onToggleView;
        this.onPatchClickListener = onPatchClickListener;
        open.setOnClickListener(this);
        patch.setNestedScrollingEnabled(false);
    }

    public static PullRequestFilesViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter,
                                                         @NonNull OnToggleView onToggleView,
                                                         @Nullable PullRequestFilesMvp.OnPatchClickListener onPatchClickListener) {
        return new PullRequestFilesViewHolder(getView(viewGroup, R.layout.pullrequest_file_row_item), viewGroup, adapter,
                onToggleView, onPatchClickListener);
    }

    @Override public void bind(@NonNull CommitFileChanges commitFileChanges) {
        CommitFileModel commit = commitFileChanges.getCommitFileModel();
        toggle.setVisibility(commit.getPatch() == null ? View.GONE : View.VISIBLE);
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
        int position = getAdapterPosition();
        onToggle(onToggleView.isCollapsed(position), false, position);
    }

    private void onToggle(boolean expanded, boolean animate, int position) {
        if (!expanded) {
            patch.swapAdapter(null, true);
            patch.setVisibility(View.GONE);
            name.setMaxLines(2);
            toggle.setRotation(0.0f);
        } else {
            if (adapter != null) {
                CommitFileChanges model = (CommitFileChanges) adapter.getItem(position);
                if (model.getLinesModel() != null && !model.getLinesModel().isEmpty()) {
                    if (model.getLinesModel().size() <= 100) {
                        patch.setAdapter(new CommitLinesAdapter(model.getLinesModel(), this));
                        patch.setVisibility(View.VISIBLE);
                    } else if (CommitFileChanges.canAttachToBundle(model)) {
                        if (adapter.getListener() != null) {
                            //noinspection unchecked
                            adapter.getListener().onItemClick(position, patch, model);
                        }
                    } else {
                        Toasty.warning(itemView.getContext(), itemView.getResources().getString(R.string.too_large_changes)).show();
                        return;
                    }
                } else {
                    patch.swapAdapter(null, true);
                    patch.setVisibility(View.GONE);
                }
            }
            name.setMaxLines(5);
            toggle.setRotation(180f);
        }
    }

    @Override public void onItemClick(int position, View v, CommitLinesModel item) {
        if (onPatchClickListener != null && adapter != null) {
            int groupPosition = getAdapterPosition();
            CommitFileChanges commitFileChanges = (CommitFileChanges) adapter.getItem(groupPosition);
            onPatchClickListener.onPatchClicked(groupPosition, position, v, commitFileChanges.getCommitFileModel(), item);
        }
    }

    @Override public void onItemLongClick(int position, View v, CommitLinesModel item) {
        if (adapter == null) return;
        int groupPosition = getAdapterPosition();
        CommitFileChanges commitFileChanges = (CommitFileChanges) adapter.getItem(groupPosition);
        int lineNo = item.getLeftLineNo() > 0 ? item.getLeftLineNo() : item.getRightLineNo();
        String url = commitFileChanges.getCommitFileModel().getBlobUrl() + "#L" + lineNo;
        AppHelper.copyToClipboard(v.getContext(), url);
    }
}

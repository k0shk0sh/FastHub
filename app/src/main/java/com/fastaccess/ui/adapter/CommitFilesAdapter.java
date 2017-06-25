package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.viewholder.PullRequestFilesViewHolder;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class CommitFilesAdapter extends BaseRecyclerAdapter<CommitFileChanges, PullRequestFilesViewHolder, BaseViewHolder
        .OnItemClickListener<CommitFileChanges>> {


    @NonNull private OnToggleView onToggleView;
    @Nullable private PullRequestFilesMvp.OnPatchClickListener onPatchClickListener;

    public CommitFilesAdapter(@NonNull ArrayList<CommitFileChanges> eventsModels,
                              @NonNull OnToggleView onToggleView,
                              @Nullable PullRequestFilesMvp.OnPatchClickListener onPatchClickListener) {
        super(eventsModels);
        this.onToggleView = onToggleView;
        this.onPatchClickListener = onPatchClickListener;
    }

    @Override protected PullRequestFilesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return PullRequestFilesViewHolder.newInstance(parent, this, onToggleView, onPatchClickListener);
    }

    @Override protected void onBindView(PullRequestFilesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}

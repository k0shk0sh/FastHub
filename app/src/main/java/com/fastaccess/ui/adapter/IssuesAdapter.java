package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.ui.adapter.viewholder.IssuesViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class IssuesAdapter extends BaseRecyclerAdapter<Issue, IssuesViewHolder, BaseViewHolder.OnItemClickListener<Issue>> {

    private boolean withAvatar;
    private boolean showRepoName;
    private boolean showState;

    public IssuesAdapter(@NonNull List<Issue> data) {
        this(data, false);
    }

    public IssuesAdapter(@NonNull List<Issue> data, boolean withAvatar) {
        super(data);
        this.withAvatar = withAvatar;
    }

    public IssuesAdapter(@NonNull List<Issue> data, boolean withAvatar, boolean showRepoName) {
        super(data);
        this.withAvatar = withAvatar;
        this.showRepoName = showRepoName;
    }

    public IssuesAdapter(@NonNull List<Issue> data, boolean withAvatar, boolean showRepoName, boolean showState) {
        super(data);
        this.withAvatar = withAvatar;
        this.showRepoName = showRepoName;
        this.showState = showState;
    }

    @Override protected IssuesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return IssuesViewHolder.newInstance(parent, this, withAvatar, showRepoName, showState);
    }

    @Override protected void onBindView(IssuesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}

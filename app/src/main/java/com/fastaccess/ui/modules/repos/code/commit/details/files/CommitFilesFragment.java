package com.fastaccess.ui.modules.repos.code.commit.details.files;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.CommitFileChanges;
import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.CommitLinesModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.ui.adapter.CommitFilesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.main.premium.PremiumActivity;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerMvp;
import com.fastaccess.ui.modules.reviews.AddReviewDialogFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 15 Feb 2017, 10:16 PM
 */

public class CommitFilesFragment extends BaseFragment<CommitFilesMvp.View, CommitFilesPresenter> implements CommitFilesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State HashMap<Long, Boolean> toggleMap = new LinkedHashMap<>();

    private CommitPagerMvp.View viewCallback;
    private CommitFilesAdapter adapter;

    public static CommitFilesFragment newInstance(@NonNull String sha, @Nullable CommitFileListModel commitFileModels) {
        CommitFilesFragment view = new CommitFilesFragment();
        if (commitFileModels != null) {
            CommitFilesSingleton.getInstance().putFiles(sha, commitFileModels);
        }
        Bundle bundle = Bundler.start().put(BundleConstant.ID, sha).end();
        view.setArguments(bundle);
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof CommitPagerMvp.View) {
            viewCallback = (CommitPagerMvp.View) getParentFragment();
        } else if (context instanceof CommitPagerMvp.View) {
            viewCallback = (CommitPagerMvp.View) context;
        }
    }

    @Override public void onDetach() {
        viewCallback = null;
        super.onDetach();
    }

    @Override public void onNotifyAdapter(@Nullable List<CommitFileChanges> items) {
        hideProgress();
        if (items != null) {
            adapter.insertItems(items);
        }
    }

    @Override public void onCommentAdded(@NonNull Comment newComment) {
        hideProgress();
        if (viewCallback != null) {
            viewCallback.onAddComment(newComment);
        }
    }

    @Override public void clearAdapter() {
        refresh.setRefreshing(true);
        adapter.clear();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        refresh.setEnabled(false);
        stateLayout.setEmptyText(R.string.no_files);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new CommitFilesAdapter(new ArrayList<>(), this, this);
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        getPresenter().onFragmentCreated(getArguments());
    }

    @NonNull @Override public CommitFilesPresenter providePresenter() {
        return new CommitFilesPresenter();
    }

    @Override public void onToggle(long position, boolean isCollapsed) {
        if (adapter.getItem((int) position).getCommitFileModel().getPatch() == null) {
            ActivityHelper.startCustomTab(getActivity(), adapter.getItem((int) position).getCommitFileModel().getBlobUrl());
        }
        toggleMap.put(position, isCollapsed);
    }

    @Override public boolean isCollapsed(long position) {
        Boolean toggle = toggleMap.get(position);
        return toggle != null && toggle;
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void onPatchClicked(int groupPosition, int childPosition, View v, CommitFileModel commit, CommitLinesModel item) {
        if (item.getText().startsWith("@@")) return;
        if (PrefGetter.isProEnabled()) {
            AddReviewDialogFragment.Companion.newInstance(item, Bundler.start().put(BundleConstant.ITEM, commit.getBlobUrl())
                    .put(BundleConstant.EXTRA, commit.getFilename())
                    .end())
                    .show(getChildFragmentManager(), "AddReviewDialogFragment");
        } else {
            PremiumActivity.Companion.startActivity(getContext());
        }
    }

    @Override public void onCommentAdded(@NonNull String comment, @NonNull CommitLinesModel item, Bundle bundle) {
        getPresenter().onSubmitComment(comment, item, bundle);
    }
}
